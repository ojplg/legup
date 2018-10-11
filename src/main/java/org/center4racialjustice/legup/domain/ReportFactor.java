package org.center4racialjustice.legup.domain;

import lombok.Data;

@Data
public class ReportFactor {
    private Long id;
    private ReportCard reportCard;
    private Bill bill;
    private VoteSide voteSide;

    @Override
    public String toString() {
        return "ReportFactor{" +
                "id=" + id +
                ", reportCardId=" + (reportCard != null ? reportCard.getId() : "null") +
                ", bill=" + bill +
                ", voteSide=" + voteSide +
                '}';
    }
}
