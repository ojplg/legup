package org.center4racialjustice.legup.domain;

import lombok.Data;

import java.util.List;

@Data
public class Organization {
    private Long id;
    private String name;
    private List<ReportCard> reportCards;
}
