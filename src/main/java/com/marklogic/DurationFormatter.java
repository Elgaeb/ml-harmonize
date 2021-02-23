package com.marklogic;

import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DurationFormatter implements Formatter<Duration> {
    @Override
    public Duration parse(String text, Locale locale) throws ParseException {
        return Duration.parse(text);
    }

    @Override
    public String print(Duration duration, Locale locale) {
        Duration absDuration = duration.abs();
        long nanoseconds = absDuration.getNano();
        long seconds = absDuration.getSeconds();
        long hoursPart = TimeUnit.SECONDS.toHours(seconds);
        long minutesPart = TimeUnit.SECONDS.toMinutes(seconds) % 60;
        long secondsPart = TimeUnit.SECONDS.toSeconds(seconds) % 60;
        long millisecondsPart = TimeUnit.NANOSECONDS.toMillis(nanoseconds);

        StringBuilder sb = new StringBuilder();
        if(seconds >= 3600) {
            sb.append(hoursPart);
            sb.append(":");
        }

        if(seconds >= 60) {
            sb.append(seconds >= 3600 ? String.format("%02d", minutesPart) : minutesPart);
            sb.append(":");
        }

        sb.append(seconds >= 60 ? String.format("%02d", secondsPart) : secondsPart);
        sb.append(String.format(".%03d", millisecondsPart));

        return duration.isNegative() ? "-" + sb.toString() : sb.toString();
    }
}
