package com.marklogic.dhf;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;

public class DateUtil {
    public static LocalDateTime fromISO(String isoDateString) {
        if (isoDateString == null) {
            return null;
        }

        try {
            TemporalAccessor ta = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(isoDateString);
            return LocalDateTime.ofInstant(Instant.from(ta), ZoneId.systemDefault());
        } catch (DateTimeParseException ex) {}

        try {
            return LocalDateTime.parse(isoDateString);
        } catch (DateTimeParseException ex) {}

        return null;
    }
}
