package org.center4racialjustice.legup.domain;

import lombok.Data;
import org.center4racialjustice.legup.util.Lists;

import java.util.List;

@Data
public class Committee {

    private Long id;
    private String name;
    private Chamber chamber;
    private String code;
    private String committeeId;
    private Long sessionNumber;
    private List<CommitteeMember> members;

    public int getMemberCount(){
        return members.size();
    }

    public CommitteeMember getMembership(Legislator legislator) {
        return Lists.findfirst(members, member -> member.getLegislator().equals(legislator));
    }
    @Override
    public String toString(){
        return  chamber + " committee: " + name + "(" + code + ")";
    }
}
