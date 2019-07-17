package org.center4racialjustice.legup.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Dates {

    public static LocalDate localDateOf(Instant instant){
        LocalDateTime localDateTime = localDateTimeOf(instant);
        return localDateTime.toLocalDate();
    }

    public static LocalDateTime localDateTimeOf(Instant instant){
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static Instant instantOf(LocalDate localDate){
        LocalDateTime dateTime = localDate.atTime(12,0);
        return Instant.from(ZonedDateTime.of(dateTime, ZoneId.systemDefault()));
    }
}
