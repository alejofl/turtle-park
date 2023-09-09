package ar.edu.itba.pod.data;

import java.time.LocalTime;
import java.util.UUID;

public record NotificationInformation(
        UUID visitorId,
        String rideName,
        int dayOfYear,
        NotificationStatus status,
        LocalTime slot,
        LocalTime newSlot,
        Integer capacity
) {
}