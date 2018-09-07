package org.center4racialjustice.legup.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ReportCard {

    private Long id;
    private String name;
    private long sessionNumber;
    private List<ReportFactor> reportFactors = new ArrayList<>();

    public void addReportFactor(ReportFactor factor){
        factor.setReportCardId(id);
        reportFactors.add(factor);
    }
}
