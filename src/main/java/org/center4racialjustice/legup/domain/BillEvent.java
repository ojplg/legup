package org.center4racialjustice.legup.domain;

import lombok.Data;
import org.center4racialjustice.legup.util.Dates;

import java.time.Instant;
import java.time.LocalDate;

@Data
public class BillEvent {

    private final LocalDate date;
    private final Chamber chamber;
    private final String rawContents;
    private final String link;

    public Instant getDateAsInstant(){
        return Dates.instantOf(date);
    }

    public BillEventKey generateEventKey(){
        return new BillEventKey(date, chamber, rawContents);
    }
}
