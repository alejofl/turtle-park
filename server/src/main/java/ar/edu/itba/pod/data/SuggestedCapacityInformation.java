package ar.edu.itba.pod.data;

import java.time.LocalTime;

public record SuggestedCapacityInformation(
        String rideName,
        int suggestedCapacity,
        LocalTime slot
) {
}
