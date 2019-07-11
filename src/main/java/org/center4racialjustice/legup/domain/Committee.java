package org.center4racialjustice.legup.domain;

import lombok.Data;

import java.util.List;

@Data
public class Committee {

    private Long id;
    private String name;
    private Chamber chamber;
    private String code;
    private Long committeeId;
    private List<CommitteeMember> members;

}
