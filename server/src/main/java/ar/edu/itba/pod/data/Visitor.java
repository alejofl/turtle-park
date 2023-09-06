package ar.edu.itba.pod.data;

import ar.edu.itba.pod.admin.PassType;

import java.time.LocalTime;
import java.util.UUID;

public class Visitor {
    private final UUID id;
    private final PassType passType;
    private int confirmedBookings;

    public Visitor(UUID id, PassType passType) {
        this.id = id;
        this.passType = passType;
        this.confirmedBookings = 0;
    }

    public boolean canBookRide(LocalTime slot) {
        switch (passType) {
            case UNLIMITED -> {
                return true;
            }
            case THREE -> {
                return confirmedBookings < 3;
            }
            case HALF_DAY -> {
                return slot.isBefore(LocalTime.of(14,0,0));
            }
            default -> {
                return false;
            }
        }
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
}
