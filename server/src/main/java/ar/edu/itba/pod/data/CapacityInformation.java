package ar.edu.itba.pod.data;

public class CapacityInformation {
    private final int pendingBookings;
    private final int confirmedBookings;
    private final int cancelledBookings;

    public CapacityInformation(int pendingBookings, int confirmedBookings, int cancelledBookings) {
        this.pendingBookings = pendingBookings;
        this.confirmedBookings = confirmedBookings;
        this.cancelledBookings = cancelledBookings;
    }

    public int getPendingBookings() {
        return pendingBookings;
    }

    public int getConfirmedBookings() {
        return confirmedBookings;
    }

    public int getCancelledBookings() {
        return cancelledBookings;
    }
}
