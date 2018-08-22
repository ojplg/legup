package org.center4racialjustice.legup.domain;

import lombok.Data;
import org.center4racialjustice.legup.util.Lists;
import org.center4racialjustice.legup.util.Tuple;

import java.util.Collections;
import java.util.List;

@Data
public class Bill implements Identifiable, Comparable<Bill> {

    private Long id;
    private long number;
    private Chamber chamber;
    private long session;

    public void setChamberFromString(String assemblyString){
        this.chamber = Chamber.fromString(assemblyString);
    }

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
}
