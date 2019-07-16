package org.center4racialjustice.legup.domain;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
public class BillEvent {

    private final LocalDate date;
    private final Chamber chamber;
    private final String rawContents;
    private final String link;

    public Instant getDateAsInstant(){
        LocalDateTime dateTime = date.atTime(12,0);
        return Instant.from(ZonedDateTime.of(dateTime, ZoneId.systemDefault()));
    }

}
