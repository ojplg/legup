package org.center4racialjustice.legup.domain;

import lombok.Data;

@Data
public class ReportFactor implements Identifiable {
    private Long id;
    private Bill bill;
    private VoteSide voteSide;
}
