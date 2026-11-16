package nutritrack.utils;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility functions for conversion between modern java.time types and legacy java.sql types,
 * as well as UI presentation formatting.
 */
public class DateUtils {

    public static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    public static final DateTimeFormatter DISPLAY_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm a");
    public static final DateTimeFormatter ISO_DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    private DateUtils() { }

    /**
     * Converts java.time.LocalDate to java.sql.Date for PreparedStatement.
     */
    public static Date toSqlDate(LocalDate localDate) {
        return (localDate != null) ? Date.valueOf(localDate) : null;
    }

    /**
     * Converts java.sql.Date to java.time.LocalDate from ResultSet.
     */
    public static LocalDate toLocalDate(Date sqlDate) {
        return (sqlDate != null) ? sqlDate.toLocalDate() : null;
    }

    /**
     * Converts java.time.LocalTime to java.sql.Time for PreparedStatement.
     */
    public static Time toSqlTime(LocalTime localTime) {
        return (localTime != null) ? Time.valueOf(localTime) : null;
    }

    /**
     * Converts java.sql.Time to java.time.LocalTime from ResultSet.
     */
    public static LocalTime toLocalTime(Time sqlTime) {
        return (sqlTime != null) ? sqlTime.toLocalTime() : null;
    }

    /**
     * Formats a LocalDate into a friendly display string (e.g., "16 Nov 2026").
     */
    public static String formatDisplayDate(LocalDate date) {
        return (date != null) ? date.format(DISPLAY_DATE_FORMAT) : "";
    }

    /**
     * Formats a LocalTime into a friendly display string (e.g., "08:30 AM").
     */
    public static String formatDisplayTime(LocalTime time) {
        return (time != null) ? time.format(DISPLAY_TIME_FORMAT) : "";
    }
}
