package ar.edu.itba.pod.data;

import ar.edu.itba.pod.server.Util;

import java.util.*;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RideForDay {
    private final String rideName;
    private final int dayOfYear;
    private final AtomicInteger capacity = new AtomicInteger(-1);
    private final Map<Visitor, BlockingQueue<NotificationInformation>> notifications = new HashMap<>();
    private final Object notificationsLock = "notificationsLock";
    private final Map<LocalTime, Queue<Util.PendingBooking>> pendingBookings = new HashMap<>();
    private final Object pendingBookingsLock = "pendingBookingsLock";
    private final Map<LocalTime, Set<Booking>> confirmedBookings = new HashMap<>();
    private final Object confirmedBookingsLock = "confirmedBookingsLock";

    public RideForDay(String rideName, int dayOfYear, LocalTime openingTime, LocalTime closingTime, int slotSize) {
        this.rideName = rideName;
        this.dayOfYear = dayOfYear;
        for (LocalTime i = openingTime; i.isBefore(closingTime); i = i.plusMinutes(slotSize)) {
            pendingBookings.put(i, new LinkedList<>());
            confirmedBookings.put(i, new HashSet<>());
        }
    }

    public boolean hasCapacity() {
        return capacity.get() != -1;
    }

    public CapacityInformation setCapacity(int capacity) {
        this.capacity.set(capacity);
        int confirmedBookingsCount = 0;
        int pendingBookingsCount = 0;
        int cancelledBookingCount = 0;
        List<LocalTime> slots;
        synchronized (pendingBookingsLock) {
            slots = pendingBookings.keySet().stream().sorted().toList();
        }
        for (LocalTime slot : slots) {
            Queue<Util.PendingBooking> pendings;
            synchronized (pendingBookingsLock) {
                pendings = pendingBookings.get(slot);
            }
            for (int i = 0; i < capacity && !pendings.isEmpty(); i++) {
                Util.PendingBooking visitor = pendings.poll();
                synchronized (confirmedBookingsLock) {
                    confirmedBookings.get(slot).add(new Booking(rideName, dayOfYear, slot, visitor.getVisitor()));
                }
                notifyConfirmedBooking(slot, visitor.getVisitor());
                visitor.getVisitor().confirmBooking();
                confirmedBookingsCount++;
            }
        }
        for (int i = 0; i < slots.size(); i++) {
            Queue<Util.PendingBooking> pendingsInSlot;
            synchronized (pendingBookingsLock) {
                pendingsInSlot = pendingBookings.get(slots.get(i));
            }
            if (!pendingsInSlot.isEmpty() && pendingsInSlot.peek().wasRelocated()) {
                continue;
            }
            for (int j = i+1; !pendingsInSlot.isEmpty() && j < slots.size(); j++) {
                int occupiedCapacityByPending, occupiedCapacityByConfirmed;
                synchronized (pendingBookingsLock) {
                    occupiedCapacityByPending = pendingBookings.get(slots.get(j)).size();
                }
                synchronized (confirmedBookingsLock) {
                    occupiedCapacityByConfirmed = confirmedBookings.get(slots.get(j)).size();
                }
                int occupiedCapacity = occupiedCapacityByPending + occupiedCapacityByConfirmed;
                while (!pendingsInSlot.isEmpty() && occupiedCapacity < capacity) {
                    Util.PendingBooking visitor = pendingsInSlot.poll();
                    if (visitor.getVisitor().getPassType() == PassType.HALF_DAY && slots.get(j).isAfter(Visitor.HALF_DAY_CUTOFF) ){
                        cancelledBookingCount++;
                        notifyCancelledBooking(slots.get(i), visitor.getVisitor());
                    } else {
                        synchronized (pendingBookingsLock) {
                            pendingBookings.get(slots.get(j)).add(new Util.PendingBooking(visitor.getVisitor(), true));
                        }
                        notifyMovedBooking(slots.get(i), slots.get(j), visitor.getVisitor());
                        pendingBookingsCount++;
                        occupiedCapacity++;
                    }
                }
            }
            while (!pendingsInSlot.isEmpty()) {
                notifyCancelledBooking(slots.get(i), pendingsInSlot.poll().getVisitor());
                cancelledBookingCount++;
            }
        }
        notifyCapacityAnnounced(capacity);
        return new CapacityInformation(pendingBookingsCount, confirmedBookingsCount, cancelledBookingCount);
    }

    private void notifyCapacityAnnounced(int capacity) {
        Set<Map.Entry<Visitor, BlockingQueue<NotificationInformation>>> entries;
        synchronized (notificationsLock) {
            entries = notifications.entrySet();
        }
        for (Map.Entry<Visitor, BlockingQueue<NotificationInformation>> entry : entries) {
            entry.getValue().add(new NotificationInformation(
                    entry.getKey().getId(),
                    rideName,
                    dayOfYear,
                    NotificationStatus.CAPACITY_ANNOUNCED,
                    null,
                    null,
                    capacity
            ));
        }
    }

    private void notifyMovedBooking(LocalTime slot, LocalTime newSlot, Visitor visitor) {
        synchronized (notificationsLock) {
            if (notifications.containsKey(visitor)) {
                notifications.get(visitor).add(new NotificationInformation(
                        visitor.getId(),
                        rideName,
                        dayOfYear,
                        NotificationStatus.BOOKING_MOVED,
                        slot,
                        newSlot,
                        null
                ));
            }
        }
    }

    private void notifyConfirmedBooking(LocalTime slot, Visitor visitor) {
        BlockingQueue<NotificationInformation> notificationQueue;
        boolean condition;
        synchronized (notificationsLock) {
            notificationQueue = notifications.get(visitor);
            condition = notifications.containsKey(visitor);
        }
        if (condition) {
            notificationQueue.add(new NotificationInformation(
                    visitor.getId(),
                    rideName,
                    dayOfYear,
                    NotificationStatus.BOOKING_CONFIRMED,
                    slot,
                    null,
                    null
            ));
            if (!hasAnotherPendingBooking(visitor)) {
                unfollowBooking(visitor);
            }
        }
    }

    private void notifyCancelledBooking(LocalTime slot, Visitor visitor) {
        BlockingQueue<NotificationInformation> notificationQueue;
        boolean condition;
        synchronized (notificationsLock) {
            notificationQueue = notifications.get(visitor);
            condition = notifications.containsKey(visitor);
        }
        if (condition) {
            notificationQueue.add(new NotificationInformation(
                    visitor.getId(),
                    rideName,
                    dayOfYear,
                    NotificationStatus.BOOKING_CANCELLED,
                    slot,
                    null,
                    null
            ));
            if (!hasAnotherPendingBooking(visitor)) {
                unfollowBooking(visitor);
            }
        }
    }

    private void notifyPendingBooking(LocalTime slot, Visitor visitor) {
        synchronized (notificationsLock) {
            if (notifications.containsKey(visitor)) {
                notifications.get(visitor).add(new NotificationInformation(
                        visitor.getId(),
                        rideName,
                        dayOfYear,
                        NotificationStatus.BOOKING_PENDING,
                        slot,
                        null,
                        null
                ));
            }
        }
    }

    private boolean hasAnotherPendingBooking(Visitor visitor) {
        boolean hasAnotherBooking = false;
        Set<Map.Entry<LocalTime, Queue<Util.PendingBooking>>> pendings;
        synchronized (pendingBookingsLock) {
            pendings = pendingBookings.entrySet();
        }
        for (Map.Entry<LocalTime, Queue<Util.PendingBooking>> entry : pendings) {
            if (entry.getValue().contains(new Util.PendingBooking(visitor))) {
                hasAnotherBooking = true;
                break;
            }
        }
        return hasAnotherBooking;
    }

    /**
     *
     * @param slot <code>LocalTime</code> for the booking
     * @param visitor <code>Visitor</code> who is booking
     * @return <code>false</code> if the visitor was added to the pending list.<br><code>true</code> if the visitor was added to the confirmed list.
     * @throws IllegalArgumentException if the booking already exists
     * @throws IllegalStateException if the requested slot is full
     */
    public boolean bookRide(LocalTime slot, Visitor visitor) {
        boolean pendingCondition, confirmedCondition;
        boolean hasAvailability;
        synchronized (pendingBookingsLock) {
            pendingCondition = pendingBookings.get(slot).contains(new Util.PendingBooking(visitor));
        }
        synchronized (confirmedBookingsLock) {
            confirmedCondition = confirmedBookings.get(slot).contains(new Booking(rideName, dayOfYear, slot, visitor));
            hasAvailability = confirmedBookings.get(slot).size() < capacity.get();
        }
        if (pendingCondition || confirmedCondition) {
            throw new IllegalArgumentException();
        }
        if (!this.hasCapacity()) {
            synchronized (pendingBookingsLock) {
                pendingBookings.get(slot).add(new Util.PendingBooking(visitor));
            }
            notifyPendingBooking(slot, visitor);
            return false;
        } else if (hasAvailability) {
            synchronized (confirmedBookingsLock) {
                confirmedBookings.get(slot).add(new Booking(rideName, dayOfYear, slot, visitor));
            }
            notifyConfirmedBooking(slot, visitor);
            visitor.confirmBooking();
            return true;
        }
        throw new IllegalStateException();
    }

    public void confirmBooking(LocalTime slot, Visitor visitor) {
        boolean pendingCondition, confirmedCondition;
        synchronized (pendingBookingsLock) {
            pendingCondition = !pendingBookings.get(slot).contains(new Util.PendingBooking(visitor));
        }
        synchronized (confirmedBookingsLock) {
            confirmedCondition = confirmedBookings.get(slot).contains(new Booking(rideName, dayOfYear, slot, visitor));
        }
        if (pendingCondition || confirmedCondition || !this.hasCapacity()) {
            throw new IllegalArgumentException();
        }
        synchronized (pendingBookingsLock) {
            pendingBookings.get(slot).remove(new Util.PendingBooking(visitor));
        }
        synchronized (confirmedBookingsLock) {
            confirmedBookings.get(slot).add(new Booking(rideName, dayOfYear, slot, visitor));
        }
        notifyConfirmedBooking(slot, visitor);
        visitor.confirmBooking();
    }

    public void cancelBooking(LocalTime slot, Visitor visitor) {
        boolean pendingCondition, confirmedCondition;
        synchronized (pendingBookingsLock) {
            pendingCondition = pendingBookings.get(slot).contains(new Util.PendingBooking(visitor));
        }
        synchronized (confirmedBookingsLock) {
            confirmedCondition = confirmedBookings.get(slot).contains(new Booking(rideName, dayOfYear, slot, visitor));
        }
        if (confirmedCondition) {
            synchronized (confirmedBookingsLock) {
                confirmedBookings.get(slot).remove(new Booking(rideName, dayOfYear, slot, visitor));
            }
            visitor.cancelBooking();
            notifyCancelledBooking(slot, visitor);
            return;
        }
        if (pendingCondition) {
            synchronized (pendingBookingsLock) {
                pendingBookings.get(slot).remove(new Util.PendingBooking(visitor));
            }
            notifyCancelledBooking(slot, visitor);
            return;
        }
        throw new IllegalArgumentException();
    }

    public Optional<AvailabilityInformation> getAvailabilityForSlot(LocalTime slot) {
        int confirmedQuantity, pendingQuantity;
        synchronized (pendingBookingsLock) {
            if (!pendingBookings.containsKey(slot)) {
                return Optional.empty();
            }
            pendingQuantity = pendingBookings.get(slot).size();
        }
        synchronized (confirmedBookingsLock) {
            confirmedQuantity = confirmedBookings.get(slot).size();
        }
        return Optional.of(new AvailabilityInformation(
                rideName,
                slot,
                confirmedQuantity,
                pendingQuantity,
                Optional.ofNullable(this.hasCapacity() ? capacity.get() : null)
        ));
    }

    public Optional<SuggestedCapacityInformation> getSuggestedCapacity() {
        if (this.hasCapacity()) {
            return Optional.empty();
        }
        Set<Map.Entry<LocalTime, Queue<Util.PendingBooking>>> pendings;
        synchronized (pendingBookingsLock) {
            pendings = pendingBookings.entrySet();
        }
        Map.Entry<LocalTime, Queue<Util.PendingBooking>> maxEntry = Collections.max(pendings,
                Comparator.comparingInt((Map.Entry<LocalTime, Queue<Util.PendingBooking>> e) -> e.getValue().size()));
        int suggestedCapacity = maxEntry.getValue().size();
        LocalTime slot = maxEntry.getKey();
        return Optional.of(new SuggestedCapacityInformation(rideName, suggestedCapacity, slot));
    }

    public Optional<List<Booking>> getConfirmedBookings() {
        List<Booking> ans = new ArrayList<>();
        Collection<Set<Booking>> bookingsList;
        synchronized (confirmedBookingsLock) {
            if (confirmedBookings.isEmpty()) {
                return Optional.empty();
            }
            bookingsList = confirmedBookings.values();
        }
        for (Set<Booking> bookings : bookingsList) {
            ans.addAll(bookings);
        }
        return Optional.of(ans);
    }

    public BlockingQueue<NotificationInformation> followBooking(Visitor visitor) {
        BlockingQueue<NotificationInformation> notificationQueue;
        synchronized (notificationsLock) {
            if (notifications.containsKey(visitor)) {
                throw new IllegalArgumentException();
            }
            notifications.put(visitor, new LinkedBlockingQueue<>());
            notificationQueue = notifications.get(visitor);
        }
        Set<Map.Entry<LocalTime, Queue<Util.PendingBooking>>> pendings;
        Set<Map.Entry<LocalTime, Set<Booking>>> confirmed;
        synchronized (pendingBookingsLock) {
            pendings = pendingBookings.entrySet();
        }
        synchronized (confirmedBookingsLock) {
            confirmed = confirmedBookings.entrySet();
        }
        for (Map.Entry<LocalTime, Queue<Util.PendingBooking>> entry : pendings) {
            if (entry.getValue().contains(new Util.PendingBooking(visitor))) {
                notifyPendingBooking(entry.getKey(), visitor);
            }
        }
        for (Map.Entry<LocalTime, Set<Booking>> entry : confirmed) {
            if (entry.getValue().contains(new Booking(rideName, dayOfYear, entry.getKey(), visitor))) {
                notifyConfirmedBooking(entry.getKey(), visitor);
            }
        }
        return notificationQueue;
    }

    public void unfollowBooking(Visitor visitor) {
        synchronized (notificationsLock) {
            if (!notifications.containsKey(visitor)) {
                throw new IllegalArgumentException();
            }
            notifications.get(visitor).add(new NotificationInformation(
                    null,
                    null,
                    0,
                    NotificationStatus.POISON_PILL,
                    null,
                    null,
                    null
            ));
            notifications.remove(visitor);
        }
    }
}
