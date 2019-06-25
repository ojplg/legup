package org.center4racialjustice.legup.domain;

import lombok.Data;
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
        int chamberCompare = this.chamber.compareTo(o.chamber);
        if ( chamberCompare != 0 ){
            return chamberCompare;
        }
        return (int) this.number - (int) o.number;
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
}
