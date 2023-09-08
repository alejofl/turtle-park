package ar.edu.itba.pod.server;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
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
}
