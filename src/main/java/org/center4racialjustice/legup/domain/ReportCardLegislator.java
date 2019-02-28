package org.center4racialjustice.legup.domain;

import lombok.Data;

@Data
public class ReportCardLegislator {
    private Long id;
    private ReportCard reportCard;
    private Legislator legislator;

    @Override
    public String toString() {
        return "ReportCardLegislator{" +
                "id=" + id +
                ", legislator=" + legislator +
                '}';
    }
}
