package ar.edu.itba.pod.data;

import java.time.LocalTime;
import java.util.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

public class Ride {
    private final String name;
    private final LocalTime openingTime;
    private final LocalTime closingTime;
    private final int slotSize;
    private final Map<Integer, RideForDay> ridesForDay = new HashMap<>();

    public Ride(String name, LocalTime openingTime, LocalTime closingTime, int slotSize) {
        this.name = name;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.slotSize = slotSize;
    }

    public String getName() {
        return name;
    }

    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public int getSlotSize() {
        return slotSize;
    }

    public synchronized CapacityInformation setCapacityForDay(int dayOfYear, int capacity) {
        ridesForDay.putIfAbsent(dayOfYear, new RideForDay(name, dayOfYear, openingTime, closingTime, slotSize));
        return ridesForDay.get(dayOfYear).setCapacity(capacity);
    }

    public synchronized boolean hasCapacityForDay(int dayOfYear) {
        return ridesForDay.containsKey(dayOfYear) && ridesForDay.get(dayOfYear).hasCapacity();
    }

    public boolean isValidSlot(LocalTime slot) {
        boolean ans = (slot.equals(openingTime) || slot.isAfter(openingTime)) && slot.isBefore(closingTime);
        for (LocalTime i = openingTime; ans && i.isBefore(closingTime); i = i.plusMinutes(slotSize)) {
            if (i.equals(slot)) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean bookForDay(int dayOfYear, LocalTime slot, Visitor visitor) {
        ridesForDay.putIfAbsent(dayOfYear, new RideForDay(name, dayOfYear, openingTime, closingTime, slotSize));
        return ridesForDay.get(dayOfYear).bookRide(slot, visitor);
    }

    public synchronized void confirmBooking(int dayOfYear, LocalTime slot, Visitor visitor) {
        if (!ridesForDay.containsKey(dayOfYear)) {
            throw new IllegalArgumentException();
        }
        ridesForDay.get(dayOfYear).confirmBooking(slot, visitor);
    }

    public synchronized void cancelBooking(int dayOfYear, LocalTime slot, Visitor visitor) {
        if (!ridesForDay.containsKey(dayOfYear)) {
            throw new IllegalArgumentException();
        }
        ridesForDay.get(dayOfYear).cancelBooking(slot, visitor);
    }

    public synchronized Optional<AvailabilityInformation> getAvailabilityForSlot(int dayOfYear, LocalTime slot) {
        return ridesForDay.get(dayOfYear).getAvailabilityForSlot(slot);
    }

    public synchronized Optional<SuggestedCapacityInformation> getSuggestedCapacity(int dayOfYear) {
        return ridesForDay.get(dayOfYear).getSuggestedCapacity();
    }

    public synchronized Optional<List<Booking>> getConfirmedBookings(int dayOfYear) {
        return ridesForDay.get(dayOfYear).getConfirmedBookings();
    }

    public synchronized BlockingQueue<NotificationInformation> followBooking(int dayOfYear, Visitor visitor) {
        ridesForDay.putIfAbsent(dayOfYear, new RideForDay(name, dayOfYear, openingTime, closingTime, slotSize));
        return ridesForDay.get(dayOfYear).followBooking(visitor);
    }

    public synchronized void unfollowBooking(int dayOfYear, Visitor visitor) {
        if (!ridesForDay.containsKey(dayOfYear)) {
            throw new IllegalArgumentException();
        }
        ridesForDay.get(dayOfYear).unfollowBooking(visitor);
    }
}
