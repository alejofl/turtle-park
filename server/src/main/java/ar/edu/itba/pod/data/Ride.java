package ar.edu.itba.pod.data;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

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
}
