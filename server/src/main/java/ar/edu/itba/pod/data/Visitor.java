package ar.edu.itba.pod.data;

import java.time.LocalTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Visitor {
    private final UUID id;
    private final PassType passType;
    private final AtomicInteger confirmedBookings = new AtomicInteger();

    public static final LocalTime HALF_DAY_CUTOFF = LocalTime.of(14,0,0);

    public Visitor(UUID id, PassType passType) {
        this.id = id;
        this.passType = passType;
    }

    public boolean canBookRide(LocalTime slot) {
        return passType.isValid(confirmedBookings.get(), slot);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Visitor visitor = (Visitor) o;

        return id.equals(visitor.id);
    }

    public UUID getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public PassType getPassType() {
        return passType;
    }

    public void confirmBooking() {
        confirmedBookings.getAndIncrement();
    }

    public void cancelBooking() {
        confirmedBookings.getAndDecrement();
    }
}
