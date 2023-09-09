package ar.edu.itba.pod.data;

import java.time.LocalTime;
import java.util.UUID;

public class Visitor {
    private final UUID id;
    private final PassType passType;
    private int confirmedBookings;

    public static final LocalTime HALF_DAY_CUTOFF = LocalTime.of(14,0,0);

    public Visitor(UUID id, PassType passType) {
        this.id = id;
        this.passType = passType;
        this.confirmedBookings = 0;
    }

    public boolean canBookRide(LocalTime slot) {
        return passType.isValid(confirmedBookings, slot);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Visitor visitor = (Visitor) o;

        return id.equals(visitor.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public PassType getPassType() {
        return passType;
    }

    public void confirmBooking() {
        confirmedBookings++;
    }

    public void cancelBooking() {
        confirmedBookings--;
    }

    public UUID getId() {
        return id;
    }
}
