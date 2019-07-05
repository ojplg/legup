package org.center4racialjustice.legup.domain;

import java.time.LocalDate;

public class BillEvent {

    private final LocalDate date;
    private final Chamber chamber;
    private final String rawContents;

    public BillEvent(LocalDate date, Chamber chamber, String rawContents){
        this.date = date;
        this.chamber = chamber;
        this.rawContents = rawContents;
    }

    public LocalDate getDate() {
        return date;
    }

    public Chamber getChamber() {
        return chamber;
    }

    public String getRawContents() {
        return rawContents;
    }
}
