package org.center4racialjustice.legup.service;

import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.BillActionLoad;
import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.BillEvent;
import org.center4racialjustice.legup.domain.BillHistory;
import org.center4racialjustice.legup.domain.CompletedBillEvent;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.illinois.BillIdentity;
import org.center4racialjustice.legup.illinois.BillSearchResults;
import org.center4racialjustice.legup.illinois.BillVotesResults;
import org.center4racialjustice.legup.illinois.SponsorName;
import org.center4racialjustice.legup.illinois.VoteResultsEventDisplay;
import org.center4racialjustice.legup.util.Tuple;

import javax.swing.plaf.basic.BasicScrollPaneUI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BillStatusComputer {

    private final BillSearchResults billSearchResults;
    private final BillHistory billHistory;

    public BillStatusComputer(BillSearchResults billSearchResults, BillHistory billHistory) {
        this.billSearchResults = billSearchResults;
        this.billHistory = billHistory;
    }

    public BillIdentity getBillIdentity(){
        return billSearchResults.getBillIdentity();
    }

    public Bill getParsedBill(){
        return billSearchResults.getParsedBill();
    }

    public BillActionLoad getMainPageLoadRecord(Bill persistedBill){
        return BillActionLoad.create(persistedBill, billSearchResults.getUrl(), billSearchResults.getChecksum());
    }

    public List<BillAction> allNonVoteActions(BillActionLoad persistedLoad){
        return billSearchResults.getNonVoteBillEvents().stream()
                .map(event -> eventToAction(event, persistedLoad))
                .collect(Collectors.toList());
    }

    public List<BillAction> unpersistedNonVoteActions(BillActionLoad persistedLoad){
        return billSearchResults.getNonVoteBillEvents().stream()
                .filter(billHistory::unrecognizedEvent)
                .map(event -> eventToAction(event, persistedLoad))
                .collect(Collectors.toList());
    }

    private BillAction eventToAction(CompletedBillEvent completedBillEvent, BillActionLoad persistedLoad){
        if( completedBillEvent.isVote() ){
            throw new IllegalArgumentException("Should not be used for votes " + completedBillEvent);
        }
        PersistableAction persistableAction = getPersistableAction(completedBillEvent);
        return persistableAction.asBillAction(persistedLoad);
    }

    private BillActionLoad eventToVoteLoad(CompletedBillEvent completedBillEvent, Bill persistedBill){
        if( ! completedBillEvent.isVote() ){
            throw new IllegalArgumentException("Should only be used for votes " + completedBillEvent);
        }
        BillVotesResults billVotesResults = billSearchResults.getBillVotesResults(completedBillEvent);
        BillActionLoad billActionLoad = BillActionLoad.create(
                persistedBill,
                billVotesResults.getVoteLinkInfo().getPdfUrl(),
                billVotesResults.getChecksum());
        return billActionLoad;
    }

    public List<Tuple<CompletedBillEvent,BillActionLoad>> allVoteActions(Bill persistedBill){
        List<Tuple<CompletedBillEvent,BillActionLoad>> loads = new ArrayList<>();
        for(CompletedBillEvent billEvent : billSearchResults.getVoteEvents()) {
            BillActionLoad billActionLoad = eventToVoteLoad(billEvent, persistedBill);
            Tuple<CompletedBillEvent, BillActionLoad> tuple = new Tuple<>(billEvent, billActionLoad);
            loads.add(tuple);
        }
        return loads;
    }

    public List<Tuple<CompletedBillEvent,BillActionLoad>> unpersistedVoteActions(Bill persistedBill){
        List<Tuple<CompletedBillEvent,BillActionLoad>> loads = new ArrayList<>();
        for(CompletedBillEvent billEvent : billSearchResults.getVoteEvents()) {
            if( billHistory.unrecognizedEvent(billEvent)) {
                BillActionLoad billActionLoad = eventToVoteLoad(billEvent, persistedBill);
                Tuple<CompletedBillEvent, BillActionLoad> tuple = new Tuple<>(billEvent, billActionLoad);
                loads.add(tuple);
            }
        }
        return loads;
    }

    public BillAction voteActionToInsert(CompletedBillEvent billEvent, BillActionLoad persistedLoad){
        BillVotesResults billVotesResults = billSearchResults.getBillVotesResults(billEvent);
        VoteResultsEventDisplay voteResultsEventDisplay = new VoteResultsEventDisplay(billEvent, billVotesResults);
        return voteResultsEventDisplay.asBillAction(persistedLoad);
    }

    public boolean hasHistory(){
        return billHistory.getBill() != null;
    }

    public Bill getPersistedBill(){
        return billHistory.getBill();
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
            BillVotesResults billVotesResults = billSearchResults.getBillVotesResults(billEventData);
            if( billVotesResults == null ){
                return new ErrorPersistableAction("Unmatched: " + billEventData);
            }
            return new VoteResultsEventDisplay(billEventData, billVotesResults);
        }
        if(BillActionType.isSponsoringEvent(billEventData.getBillActionType()) ){
            return new SponsorshipPersistableAction(billEventData);
        }
        if( CommitteePersistableAction.supports(billEventData.getBillActionType()) ){
            return new CommitteePersistableAction(billEventData);
        }

        return new DefaultPersistableAction(billEventData);
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
