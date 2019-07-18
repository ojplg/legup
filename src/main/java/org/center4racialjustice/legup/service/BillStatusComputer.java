package org.center4racialjustice.legup.service;

import org.center4racialjustice.legup.domain.BillActionLoad;
import org.center4racialjustice.legup.domain.BillEvent;
import org.center4racialjustice.legup.domain.BillHistory;
import org.center4racialjustice.legup.illinois.BillSearchResults;

import java.util.ArrayList;
import java.util.List;

public class BillStatusComputer {

    private final BillSearchResults billSearchResults;
    private final BillHistory billHistory;

    public BillStatusComputer(BillSearchResults billSearchResults, BillHistory billHistory) {
        this.billSearchResults = billSearchResults;
        this.billHistory = billHistory;
    }

    public boolean hasHistory(){
        return billHistory.getBill() != null;
    }

    public List<BillActionLoad> getPriorLoads(){
        return billHistory.getPriorLoads();
    }

    public List<BillEvent> unpersistedEvents(){
        List<BillEvent> unpersisted = new ArrayList<>();
        for(BillEvent billEvent : billSearchResults.getRawBillEvents()){
            if( ! billHistory.recognizedEvent(billEvent) ){
                unpersisted.add(billEvent);
            }
        }
        return unpersisted;
    }

    public boolean hasUnpersistedEvents(){
        return unpersistedEvents().size() > 0;
    }
}
