package org.center4racialjustice.legup.domain;

import lombok.Data;

@Data
public class Legislator implements Comparable<Legislator> {

    private Long id;
    private String unparsedName;
    private String lastName;
    private String firstName;
    private String suffix;
    private String middleInitialOrName;
    private String party;
    private Chamber chamber;
    private long district;
    private long sessionNumber;
    private String memberId;

    public void setName(Name name){
        this.unparsedName = name.getUnparsedName();
        this.lastName = name.getLastName();
        this.firstName = name.getFirstName();
        this.suffix = name.getSuffix();
        this.middleInitialOrName = name.getMiddleInitial();
    }

    public Name getName(){
        return new Name(unparsedName, firstName, middleInitialOrName, lastName, null, suffix);
    }

    public String getMemberLink(){
        return "http://www.ilga.gov/house/Rep.asp?GA=100&MemberID=" + memberId;
    }

    @Override
    public int compareTo(Legislator that) {
        int comparison = this.lastName.compareTo(that.lastName);
        if ( comparison != 0 ){
            return comparison;
        }
        comparison = this.firstName.compareTo(that.firstName);
        if ( comparison != 0 ){
            return comparison;
        }
        if( this.middleInitialOrName != null) {
            comparison = this.middleInitialOrName.compareTo(that.middleInitialOrName);
            if (comparison != 0) {
                return comparison;
            }
        }
        return 0;
    }
}
