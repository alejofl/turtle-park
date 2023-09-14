package ar.edu.itba.pod.server;

import ar.edu.itba.pod.data.Visitor;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.Optional;

public class Util {
    public static DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static Optional<LocalTime> checkTimeFormat(String time) {
        try {
            return Optional.of(LocalTime.parse(time, TIME_FORMATTER));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    public static boolean isValidDayOfYear(int dayOfYear) {
        return dayOfYear > 0 && dayOfYear < 366;
    }

    public static class PendingBooking {
        private final Visitor visitor;
        private final boolean relocated;

        public PendingBooking(Visitor key, boolean value) {
            if (key == null) {
                throw new IllegalArgumentException();
            }
            this.visitor = key;
            this.relocated = value;
        }

        public PendingBooking(Visitor key) {
            if (key == null) {
                throw new IllegalArgumentException();
            }
            this.visitor = key;
            this.relocated = false;
        }

        public Visitor getVisitor() {
            return visitor;
        }

        public boolean wasRelocated() {
            return relocated;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PendingBooking that = (PendingBooking) o;
            return Objects.equals(visitor, that.visitor);
        }

        @Override
        public int hashCode() {
            return Objects.hash(visitor);
        }
    }
}
