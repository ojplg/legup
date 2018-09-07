package org.center4racialjustice.legup.domain;

import lombok.Data;

@Data
public class ReportFactor {
    private Long id;
    private Long reportCardId;
    private Bill bill;
    private VoteSide voteSide;
}
