package org.center4racialjustice.legup.service;

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

    public boolean hasNoHistory(){
        return billHistory.getBill() == null;
    }

    public List<BillEvent> unpersistedEvents(){
        List<BillEvent> unpersisted = new ArrayList<>();
        for(BillEvent billEvent : billSearchResults.getBillEvents()){
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