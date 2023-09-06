package ar.edu.itba.pod.data;

import java.util.*;

public class Park {

    private static Park park = null;
    private final Set<Ride> rides = new HashSet<>();
    private final Map<Integer, List<Visitor>> visitors = new HashMap<>();

    private Park() {

    }

    public static synchronized Park getInstance() {
        if (park == null) {
            park = new Park();
        }
        return park;
    }
}
