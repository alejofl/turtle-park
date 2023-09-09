package ar.edu.itba.pod.data;

import ar.edu.itba.pod.query.SuggestedCapacity;
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
        if (!Util.isValidDayOfYear(dayOfYear) ||
                visitors.getOrDefault(dayOfYear, new HashMap<>()).containsKey(visitorId)
        ) {
            throw new IllegalArgumentException();
        }
        visitors.putIfAbsent(dayOfYear, new HashMap<>());
        visitors.get(dayOfYear).put(visitorId, new Visitor(visitorId, passType));
    }

    public synchronized CapacityInformation loadRideCapacity(String rideName, int dayOfYear, int capacity) {
        if (!rides.containsKey(rideName) ||
            !Util.isValidDayOfYear(dayOfYear) ||
            capacity < 0 ||
            rides.get(rideName).hasCapacityForDay(dayOfYear)) {
            throw new IllegalArgumentException();
        }
        return rides.get(rideName).setCapacityForDay(dayOfYear, capacity);
    }

    /*
        Si el día es válido entre 1 y 365. Si el día tiene pases creados.
        Si en ese dia hay un pase para el visitante.
        Si en ese día y slot puede reservarlo teniendo en cuenta su pase.
        Si existe la atracción.
        Si en esa atracción el slot que se paso como argumento es válida.
    */
    private boolean canOperate(String rideName, int dayOfYear, LocalTime slot, UUID visitorId) {
        return Util.isValidDayOfYear(dayOfYear) && visitors.containsKey(dayOfYear) &&
                visitors.get(dayOfYear).containsKey(visitorId) &&
                visitors.get(dayOfYear).get(visitorId).canBookRide(slot) && rides.containsKey(rideName) &&
                rides.get(rideName).isValidSlot(slot);
    }

    /*
        La segunda condición hace referencia al caso en el no existan pases para un día.
        Es decir nunca se creo el par key-value para ese dia con el mapa con los pases.
     */
    public boolean bookRide(String rideName, int dayOfYear, LocalTime slot, UUID visitorId) {
        if (!canOperate(rideName, dayOfYear, slot, visitorId)) {
            throw new IllegalArgumentException();
        }
        return rides.get(rideName).bookForDay(dayOfYear, slot, visitors.get(dayOfYear).get(visitorId));
    }

    public Collection<Ride> getRides() {
        return rides.values();
    }

    public void confirmBooking(String rideName, int dayOfYear, LocalTime slot, UUID visitorId) {
        if (!canOperate(rideName, dayOfYear, slot, visitorId)) {
            throw new IllegalArgumentException();
        }
        rides.get(rideName).confirmBooking(dayOfYear, slot, visitors.get(dayOfYear).get(visitorId));
    }

    public void cancelBooking(String rideName, int dayOfYear, LocalTime slot, UUID visitorId) {
        if (!canOperate(rideName, dayOfYear, slot, visitorId)) {
            throw new IllegalArgumentException();
        }
        rides.get(rideName).cancelBooking(dayOfYear, slot, visitors.get(dayOfYear).get(visitorId));
    }

    public Optional<AvailabilityInformation> getAvailabilityForSlot(String rideName, int dayOfYear, LocalTime slot) {
        if (!rides.containsKey(rideName) || !Util.isValidDayOfYear(dayOfYear)) {
            throw new IllegalArgumentException();
        }
        return rides.get(rideName).getAvailabilityForSlot(dayOfYear, slot);
    }

    public List<AvailabilityInformation> getAvailabilityForSlot(String rideName, int dayOfYear, LocalTime startingSlot, LocalTime endingSlot) {
        if (endingSlot.isBefore(startingSlot)) {
            throw new IllegalArgumentException();
        }
        List<AvailabilityInformation> ans = new ArrayList<>();
        for (LocalTime i = startingSlot; i.isBefore(endingSlot); i = i.plusMinutes(rides.get(rideName).getSlotSize())) {
            getAvailabilityForSlot(rideName, dayOfYear, i).ifPresent(ans::add);
        }
        return ans;
    }

    public List<AvailabilityInformation> getAvailabilityForSlot(int dayOfYear, LocalTime startingSlot, LocalTime endingSlot) {
        List<AvailabilityInformation> ans = new ArrayList<>();
        for(String r : rides.keySet()) {
            ans.addAll(getAvailabilityForSlot(r, dayOfYear, startingSlot, endingSlot));
        }
        return ans;
    }

    public List<SuggestedCapacityInformation> getSuggestedCapacities(int dayOfYear) {
        if (!Util.isValidDayOfYear(dayOfYear)) {
            throw new IllegalArgumentException();
        }
        List<SuggestedCapacityInformation> ans = new ArrayList<>();
        for (Ride r : rides.values()) {
            r.getSuggestedCapacity(dayOfYear).ifPresent(ans::add);
        }
        return ans;
    }

    public List<Booking> getConfirmedBookings(int dayOfYear) {
        if (!Util.isValidDayOfYear(dayOfYear)) {
            throw new IllegalArgumentException();
        }
        List<Booking> ans = new ArrayList<>();
        for (Ride r : rides.values()) {
            r.getConfirmedBookings(dayOfYear).ifPresent(ans::addAll);
        }
        Collections.sort(ans);
        return ans;
    }
}
