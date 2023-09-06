package ar.edu.itba.pod.data;

import java.util.*;
import java.time.LocalTime;

public class RideForDay {
    private Integer capacity = null;
    private final Set<Visitor> notifications = new HashSet<>();
    private final Map<LocalTime, List<Visitor>> pendingBookings = new HashMap<>();
    private final Map<LocalTime, Set<Visitor>> confirmedBookings = new HashMap<>();

    public RideForDay(LocalTime openingTime, LocalTime closingTime, int slotSize) {
        for (LocalTime i = openingTime; i.isBefore(closingTime); i = i.plusMinutes(slotSize)) {
            pendingBookings.put(i, new LinkedList<>());
            confirmedBookings.put(i, new HashSet<>());
        }
    }

    public boolean hasCapacity() {
        return capacity != null;
    }

    public synchronized void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Integer getCapacity() {
        return capacity;
    }

    /**
     *
     * @param slot
     * @param visitor
     * @return false when user added to pending list and true when user added is in confirmed list.
     */
    public boolean bookRide(LocalTime slot, Visitor visitor) {
        if (pendingBookings.get(slot).contains(visitor) || confirmedBookings.get(slot).contains(visitor)) {
            throw new IllegalArgumentException();
        }
        if (capacity == null) {
            pendingBookings.get(slot).add(visitor);
            return false;
        } else if (confirmedBookings.get(slot).size() < capacity) {
            confirmedBookings.get(slot).add(visitor);
            return true;
        }
        throw new IllegalStateException();
    }
}
