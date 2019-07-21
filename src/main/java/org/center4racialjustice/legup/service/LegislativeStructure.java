package org.center4racialjustice.legup.service;

import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Committee;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.util.Lists;

import java.util.List;

public class LegislativeStructure {

    private final List<Legislator> legislators;
    private final List<Committee> committees;

    public LegislativeStructure(List<Legislator> legislators, List<Committee> committees) {
        this.legislators = legislators;
        this.committees = committees;
    }

    public Legislator findLegislatorByName(Name name) {
        return Lists.findfirst(legislators, l -> l.matchesName(name));
    }

    public Legislator findByNameAndChamber(Name name, Chamber chamber){
        return Lists.findfirst(legislators, l -> l.matchesName(name) && chamber.equals(l.getChamber()));
    }

    public Legislator findLegislatorByMemberID(String memberId){
        if ( memberId == null ){
            return null;
        }
        return Lists.findfirst(legislators, l -> memberId.equals(l.getMemberId()));
    }
}
