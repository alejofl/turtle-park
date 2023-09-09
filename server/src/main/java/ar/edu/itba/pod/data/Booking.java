package ar.edu.itba.pod.data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public class Booking implements Comparable<Booking> {
    private final String rideName;
    private final int dayOfYear;
    private final LocalTime slot;
    private final LocalDateTime timestamp;
    private final Visitor visitor;

    public Booking(String rideName, int dayOfYear, LocalTime slot, Visitor visitor) {
        this.rideName = rideName;
        this.dayOfYear = dayOfYear;
        this.slot = slot;
        this.timestamp = LocalDateTime.now();
        this.visitor = visitor;
    }

    @Override
    public int compareTo(Booking o) {
        return timestamp.compareTo(o.timestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Booking booking = (Booking) o;

        if (dayOfYear != booking.dayOfYear) return false;
        if (!rideName.equals(booking.rideName)) return false;
        if (!slot.equals(booking.slot)) return false;
        return visitor.equals(booking.visitor);
    }

    @Override
    public int hashCode() {
        int result = rideName.hashCode();
        result = 31 * result + dayOfYear;
        result = 31 * result + slot.hashCode();
        result = 31 * result + timestamp.hashCode();
        result = 31 * result + visitor.hashCode();
        return result;
    }

    public String getRideName() {
        return rideName;
    }

    public int getDayOfYear() {
        return dayOfYear;
    }

    public LocalTime getSlot() {
        return slot;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Visitor getVisitor() {
        return visitor;
    }
}
