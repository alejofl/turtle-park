package ar.edu.itba.pod.data;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
        ridesForDay.putIfAbsent(dayOfYear, new RideForDay(openingTime, closingTime, slotSize));
        return ridesForDay.get(dayOfYear).setCapacity(capacity);
    }

    public boolean hasCapacityForDay(int dayOfYear) {
        return ridesForDay.containsKey(dayOfYear) && ridesForDay.get(dayOfYear).hasCapacity();
    }

    public boolean isValidSlot(LocalTime slot) {
        boolean ans = slot.isAfter(openingTime) && slot.isBefore(closingTime);
        for (LocalTime i = openingTime; ans && i.isBefore(closingTime); i = i.plusMinutes(slotSize)) {
            if (i.equals(slot)) {
                return true;
            }
        }
        return false;
    }

    public boolean bookForDay(int dayOfYear, LocalTime slot, Visitor visitor) {
        ridesForDay.putIfAbsent(dayOfYear, new RideForDay(openingTime, closingTime, slotSize));
        return ridesForDay.get(dayOfYear).bookRide(slot, visitor);
    }
}
