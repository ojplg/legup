package org.center4racialjustice.legup.domain;

import lombok.Data;
import org.center4racialjustice.legup.illinois.BillIdentity;
import org.center4racialjustice.legup.illinois.LegislationType;
import org.center4racialjustice.legup.util.Lists;
import org.center4racialjustice.legup.util.Tuple;

import java.util.Collections;
import java.util.List;

@Data
public class Bill implements Comparable<Bill> {

    private Long id;
    private long number;
    private Chamber chamber;
    private String legislationSubType;
    private String shortDescription;
    private long session;

    @Override
    public int compareTo(Bill o) {
        if ( this.session != o.session ){
            return (int) this.session - (int) o.session;
        }
        int chamberCompare = this.chamber.compareTo(o.chamber);
        if ( chamberCompare != 0 ){
            return chamberCompare;
        }
        int subTypeCompare = this.legislationSubType.compareTo(o.legislationSubType);
        if ( subTypeCompare != 0 ){
            return subTypeCompare;
        }
        return (int) this.number - (int) o.number;
    }

    public static String formKey(long session, Chamber chamber, long number){
        return session + "." + chamber + "." + number;
    }

    public static Tuple<List<Bill>, List<Bill>> divideAndOrder(List<Bill> bills){
        Tuple<List<Bill>, List<Bill>> dividedBills =
                Lists.divide(bills, b -> b.getChamber().equals(Chamber.House));
        Collections.sort(dividedBills.getFirst());
        Collections.sort(dividedBills.getSecond());
        return dividedBills;
    }

    public String getShortBillId(){
        return chamber + "." + number;
    }

    public BillIdentity getBillIdentity(){
        return new BillIdentity(session, chamber, getLegislationType(), number);
    }

    public LegislationType getLegislationType(){
        return LegislationType.fromChamberAndSubType(chamber, legislationSubType);
    }

    public void setLegislationType(LegislationType legislationType){
        this.chamber = legislationType.getChamber();
        this.legislationSubType = legislationType.getSubType();
    }

    public void setLegislationIdentity(LegislationIdentity legislationIdentity){
        this.chamber = legislationIdentity.getChamber();
        this.legislationSubType = legislationIdentity.getLegislationSubType();
        this.number = legislationIdentity.getNumber();
    }

    public String getKey(){
        return formKey(this.session, this.chamber, this.number);
    }
}
