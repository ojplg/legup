package org.center4racialjustice.legup.domain;

import lombok.Data;

@Data
public class ReportCardLegislator {
    private Long id;
    private ReportCard reportCard;
    private Legislator legislator;
}
