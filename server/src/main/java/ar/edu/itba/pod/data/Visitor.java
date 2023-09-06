package ar.edu.itba.pod.data;

import java.util.UUID;

public class Visitor {
    private final UUID id;
    private final PassType passType;
    private int confirmedBookings;

    public Visitor(UUID id, PassType passType) {
        this.id = id;
        this.passType = passType;
    }
}
