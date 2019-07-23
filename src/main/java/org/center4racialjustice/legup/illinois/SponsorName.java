package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.BillActionLoad;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.service.PersistableAction;

import java.util.Collections;
import java.util.List;

public class SponsorName implements PersistableAction {

    private final String rawName;
    private final String memberId;
    private Legislator legislator;

    public SponsorName(String rawName, String memberId){
        this.rawName = rawName;
        this.memberId = memberId;
        this.legislator = null;
    }

    public boolean matches(Legislator legislator){
        return legislator.getMemberId().equals(memberId);
    }

    public boolean isComplete(){
        return legislator != null;
    }

    public void complete(Legislator legislator){
        if( this.legislator != null ){
            throw new RuntimeException("Legislator already assigned to "
                    + this + " Why adding: " + legislator);
        }
        if ( ! matches(legislator) ){
            throw new RuntimeException("Cannot complete " + this
                + " with " + legislator);
        }
        this.legislator = legislator;
    }

    public String getRawName() {
        return rawName;
    }

    public String getMemberId() {
        return memberId;
    }

    public Legislator getLegislator() {
        return legislator;
    }

    public Name getLegislatorName(){
        return legislator.getName();
    }

    public boolean matchesLegislatorName(Name name){
        return getLegislatorName().matches(name);
    }

    public boolean matchesMemberID(String idToTest){
        return this.memberId.equals(idToTest);
    }

    @Override
    public String toString() {
        return "SponsorName{" +
                "rawName='" + rawName + '\'' +
                ", memberId='" + memberId + '\'' +
                ", legislator=" + legislator +
                '}';
    }

    @Override
    public String getDisplay() {
        return "Name: " + rawName + " matched " + legislator.getDisplay();
    }

    @Override
    public List<String> getErrors() {
        if( legislator == null ){
            return Collections.singletonList("Unmatched: " + rawName);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public BillAction asBillAction(BillActionLoad persistedLoad) {
        throw new UnsupportedOperationException();
    }
}
