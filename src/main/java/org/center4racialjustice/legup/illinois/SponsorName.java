package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Legislator;

public class SponsorName {

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

    @Override
    public String toString() {
        return "SponsorName{" +
                "rawName='" + rawName + '\'' +
                ", memberId='" + memberId + '\'' +
                ", legislator=" + legislator +
                '}';
    }
}
