package ar.edu.itba.pod.data;

import java.time.LocalTime;

public enum PassType {
    UNLIMITED {
        @Override
        boolean isValid(int confirmedBookings, LocalTime slot) {
            return true;
        }
    },
    THREE {
        @Override
        boolean isValid(int confirmedBookings, LocalTime slot) {
            return confirmedBookings < 3;
        }
    },
    HALF_DAY {
        @Override
        boolean isValid(int confirmedBookings, LocalTime slot) {
            return slot.isBefore(Visitor.HALF_DAY_CUTOFF);
        }
    };

    abstract boolean isValid(int confirmedBookings, LocalTime slot);
}
