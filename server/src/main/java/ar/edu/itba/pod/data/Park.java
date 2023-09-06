package ar.edu.itba.pod.data;

import ar.edu.itba.pod.admin.PassType;
import ar.edu.itba.pod.server.Util;

import java.time.LocalTime;
import java.util.*;

import static java.time.temporal.ChronoUnit.MINUTES;


public class Park {

    private static Park park = null;
    private final Map<String, Ride> rides = new HashMap<>();
    private final Map<Integer, Map<UUID, Visitor>> visitors = new HashMap<>();

    private Park() {

    }

    public static synchronized Park getInstance() {
        if (park == null) {
            park = new Park();
        }
        return park;
    }

    public synchronized void addRide(String rideName, LocalTime openingTime, LocalTime closingTime, int slotSize) {
        if (rides.containsKey(rideName) || slotSize < 0 ||
                openingTime.isAfter(closingTime) ||
                MINUTES.between(openingTime, closingTime) < slotSize) {
            throw new IllegalArgumentException();
        }
        rides.put(rideName, new Ride(rideName, openingTime, closingTime, slotSize));
    }

    public synchronized void addPass(UUID visitorId, PassType passType, int dayOfYear) {
        if (passType == PassType.UNDEFINED ||
                passType == PassType.UNRECOGNIZED ||
                !Util.isValidDayOfYear(dayOfYear) ||
                visitors.getOrDefault(dayOfYear, new HashMap<>()).containsKey(visitorId)
        ) {
            throw new IllegalArgumentException();
        }
        visitors.putIfAbsent(dayOfYear, new HashMap<>());
        visitors.get(dayOfYear).put(visitorId, new Visitor(visitorId, passType));
    }

    public synchronized void loadRideCapacity(String rideName, int dayOfYear, int capacity) {
        if (!rides.containsKey(rideName) ||
            !Util.isValidDayOfYear(dayOfYear) ||
            capacity < 0 ||
            rides.get(rideName).hasCapacityForDay(dayOfYear)) {
            throw new IllegalArgumentException();
        }
        rides.get(rideName).setCapacityForDay(dayOfYear, capacity);
    }


    public boolean bookRide(String rideName, int dayOfYear, LocalTime slot, UUID visitorId) {
        if (!Util.isValidDayOfYear(dayOfYear) || !visitors.containsKey(dayOfYear) ||
                !visitors.get(dayOfYear).containsKey(visitorId) ||
                !visitors.get(dayOfYear).get(visitorId).canBookRide(slot) || !rides.containsKey(rideName) ||
                !rides.get(rideName).isValidSlot(slot)
        ) {
            throw new IllegalArgumentException();
        }
        return rides.get(rideName).bookForDay(dayOfYear, slot, visitors.get(dayOfYear).get(visitorId));
    }
}
