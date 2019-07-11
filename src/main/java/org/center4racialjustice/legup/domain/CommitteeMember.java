package org.center4racialjustice.legup.domain;

import lombok.Data;

@Data
public class CommitteeMember {

    private Long id;
    private String title;
    private Legislator legislator;

}
