package ar.edu.itba.pod.data;

public record CapacityInformation (
    int pendingBookings,
    int confirmedBookings,
    int cancelledBookings
) {

}
