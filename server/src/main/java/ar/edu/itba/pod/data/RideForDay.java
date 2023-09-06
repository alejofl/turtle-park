package ar.edu.itba.pod.data;

import java.util.*;
import java.time.LocalTime;

public class RideForDay {
    private int capacity;
    private final Set<Visitor> notifications = new HashSet<>();
    private final Map<LocalTime, List<Visitor>> pendingBookings = new HashMap<>();
    private final Map<LocalTime, List<Visitor>> confirmedBookings = new HashMap<>();

    public RideForDay() {
    }
}
