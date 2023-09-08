package ar.edu.itba.pod.data;

import java.time.LocalTime;
import java.util.Optional;

public record AvailabilityInformation(
        String rideName,
        LocalTime slot,
        int confirmedBookings,
        int pendingBookings,
        Optional<Integer> capacity
) {
}
