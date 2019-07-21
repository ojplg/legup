package org.center4racialjustice.legup.domain;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RawBillEvent {
    private final LocalDate date;
    private final Chamber chamber;
    private final String rawContents;
    private final String link;
}
