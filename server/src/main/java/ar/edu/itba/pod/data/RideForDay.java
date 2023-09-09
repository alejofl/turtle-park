package ar.edu.itba.pod.data;

import javax.swing.text.html.Option;
import java.awt.print.Book;
import java.util.*;
import java.time.LocalTime;
import java.util.function.Consumer;

public class RideForDay {
    private final String rideName;
    private final int dayOfYear;
    private Integer capacity = null;
    private final Set<Visitor> notifications = new HashSet<>();
    private final Map<LocalTime, Queue<Visitor>> pendingBookings = new HashMap<>();
    private final Map<LocalTime, Set<Booking>> confirmedBookings = new HashMap<>();

    public RideForDay(String rideName, int dayOfYear, LocalTime openingTime, LocalTime closingTime, int slotSize) {
        this.rideName = rideName;
        this.dayOfYear = dayOfYear;
        for (LocalTime i = openingTime; i.isBefore(closingTime); i = i.plusMinutes(slotSize)) {
            pendingBookings.put(i, new LinkedList<>());
            confirmedBookings.put(i, new HashSet<>());
        }
    }

    public boolean hasCapacity() {
        return capacity != null;
    }

    public synchronized CapacityInformation setCapacity(int capacity) {
        this.capacity = capacity;
        int confirmedBookingsCount = 0;
        int pendingBookingsCount = 0;
        int cancelledBookingCount = 0;
        List<LocalTime> slots = pendingBookings.keySet().stream().sorted().toList();
        for (LocalTime slot : slots) {
            Queue<Visitor> pendings = pendingBookings.get(slot);
            for (int i = 0; i < capacity && !pendings.isEmpty(); i++) {
                Visitor visitor = pendings.poll();
                confirmedBookings.get(slot).add(new Booking(rideName, dayOfYear, slot, visitor));
                visitor.confirmBooking();
                confirmedBookingsCount++;
            }
        }
        for (int i = 0; i < slots.size(); i++) {
            Queue<Visitor> pendings = pendingBookings.get(slots.get(i));
            for (int j = i+1; !pendings.isEmpty() && j < slots.size(); j++) {
                int occupiedCapacity = pendingBookings.get(slots.get(j)).size() + confirmedBookings.get(slots.get(j)).size();
                while (!pendings.isEmpty() && occupiedCapacity < capacity) {
                    Visitor visitor = pendings.poll();
                    if (visitor.getPassType() == PassType.HALF_DAY && slots.get(j).isAfter(Visitor.HALF_DAY_CUTOFF) ){
                        cancelledBookingCount++;
                    } else {
                        pendingBookings.get(slots.get(j)).add(visitor);
                        pendingBookingsCount++;
                        occupiedCapacity++;
                    }
                }
            }
            while (!pendings.isEmpty()) {
                pendings.poll();
                cancelledBookingCount++;
            }
        }
        return new CapacityInformation(pendingBookingsCount, confirmedBookingsCount, cancelledBookingCount);
    }


    public Integer getCapacity() {
        return capacity;
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
        if (pendingBookings.get(slot).contains(visitor) || confirmedBookings.get(slot).contains(new Booking(rideName, dayOfYear, slot, visitor))) {
            throw new IllegalArgumentException();
        }
        if (capacity == null) {
            pendingBookings.get(slot).add(visitor);
            return false;
        } else if (confirmedBookings.get(slot).size() < capacity) {
            confirmedBookings.get(slot).add(new Booking(rideName, dayOfYear, slot, visitor));
            visitor.confirmBooking();
            return true;
        }
        throw new IllegalStateException();
    }

    public void confirmBooking(LocalTime slot, Visitor visitor) {
        if (confirmedBookings.get(slot).contains(new Booking(rideName, dayOfYear, slot, visitor)) ||
            capacity == null || !pendingBookings.get(slot).contains(visitor)) {
            throw new IllegalArgumentException();
        }
        pendingBookings.get(slot).remove(visitor);
        confirmedBookings.get(slot).add(new Booking(rideName, dayOfYear, slot, visitor));
        visitor.confirmBooking();
    }

    public void cancelBooking(LocalTime slot, Visitor visitor) {
        if (confirmedBookings.get(slot).contains(new Booking(rideName, dayOfYear, slot, visitor))) {
            confirmedBookings.get(slot).remove(new Booking(rideName, dayOfYear, slot, visitor));
            visitor.cancelBooking();
            return;
        }
        if (pendingBookings.get(slot).contains(visitor)) {
            pendingBookings.get(slot).remove(visitor);
            return;
        }
        throw new IllegalArgumentException();
    }

    public Optional<AvailabilityInformation> getAvailabilityForSlot(LocalTime slot) {
        if (!pendingBookings.containsKey(slot)) {
            return Optional.empty();
        }
        return Optional.of(new AvailabilityInformation(
                rideName,
                slot,
                confirmedBookings.get(slot).size(),
                pendingBookings.get(slot).size(),
                Optional.ofNullable(capacity)
        ));
    }

    public Optional<SuggestedCapacityInformation> getSuggestedCapacity() {
        if (capacity == null) {
            return Optional.empty();
        }
        Map.Entry<LocalTime, Queue<Visitor>> maxEntry = Collections.max(pendingBookings.entrySet(),
                (Map.Entry<LocalTime, Queue<Visitor>> e1, Map.Entry<LocalTime, Queue<Visitor>> e2) -> Integer.valueOf(e1.getValue().size())
                .compareTo(e2.getValue().size()));
        int suggestedCapacity = maxEntry.getValue().size();
        LocalTime slot = maxEntry.getKey();
        return Optional.of( new SuggestedCapacityInformation(rideName, suggestedCapacity, slot));
    }

    public Optional<List<Booking>> getConfirmedBookings() {
        if (confirmedBookings.isEmpty()) {
            return null;
        }
        List<Booking> ans = new ArrayList<>();
        for (Set<Booking> bookings : confirmedBookings.values()) {
            for (Booking b : bookings) {
                ans.add(b);
            }
        }
        return Optional.of(ans);
    }
}
