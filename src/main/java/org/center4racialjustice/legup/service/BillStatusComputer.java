package org.center4racialjustice.legup.service;

import org.center4racialjustice.legup.domain.BillActionLoad;
import org.center4racialjustice.legup.domain.BillHistory;
import org.center4racialjustice.legup.domain.CompletedBillEvent;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.illinois.BillSearchResults;
import org.center4racialjustice.legup.illinois.BillVotesResults;
import org.center4racialjustice.legup.illinois.SponsorName;

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

    public boolean hasUncollatedVotes(){
        return billSearchResults.getUncollatedVotes().size() > 0;
    }

    public List<Name> getUncollatedVotes(){
        return billSearchResults.getUncollatedVotes();
    }

    public boolean hasUncollatedSponsors(){
        return billSearchResults.getUncollatedSponsors().size() > 0;
    }

    public List<SponsorName> getUncollatedSponsors(){
        return billSearchResults.getUncollatedSponsors();
    }

    public List<CompletedBillEvent> getUnpersistedEvents(){
        List<CompletedBillEvent> unpersisted = new ArrayList<>();
        for(CompletedBillEvent billEvent : billSearchResults.getBillEvents()){
            if( ! billHistory.recognizedEvent(billEvent) ){
                unpersisted.add(billEvent);
            }
        }
        return unpersisted;
    }

    public boolean hasUnpersistedEvents(){
        return getUnpersistedEvents().size() > 0;
    }

    public PersistableAction getPersistableAction(CompletedBillEvent billEventData){
        if( billEventData.isVote() ){
            BillVotesResults billVotesResults = billSearchResults.getBillVotesResults(billEventData.getBillEvent());
            if( billVotesResults == null ){
                return new ErrorPersistableAction("Unmatched: " + billEventData);
            }
            return billVotesResults;
        }
        if( billEventData.isSponsorship() || billEventData.isChiefSponsorship() ){
            return new SponsorshipPersistableAction(billEventData);
        }
        if( CommitteePersistableAction.supports(billEventData.getBillActionType()) ){
            return new CommitteePersistableAction(billEventData);
        }

        return new DefaultPersistableAction();
    }

    public String getPersistableActionDisplay(CompletedBillEvent billEventData){
        PersistableAction persistableAction = getPersistableAction(billEventData);
        return persistableAction.getDisplay();
    }

    public List<String> getErrors(){
        return billSearchResults.getErrors();
    }

    public boolean hasErrors(){
        return getErrors().size() > 0;
    }
}
