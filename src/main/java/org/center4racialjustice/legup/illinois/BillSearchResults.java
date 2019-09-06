package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.BillEventKey;
import org.center4racialjustice.legup.domain.CompletedBillEvent;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.service.LegislativeStructure;
import org.center4racialjustice.legup.util.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BillSearchResults {

//    private static final Logger log = LogManager.getLogger(BillSearchResults.class);

    private final Bill parsedBill;
    private final SponsorNames sponsorNames;
    private final long checksum;
    private final String url;
    private final List<BillVotesResults> votesResults;
    private final List<CompletedBillEvent> billEvents;

    public BillSearchResults(BillHtmlParser billHtmlParser,
                             LegislativeStructure legislativeStructure,
                             List<BillVotesResults> votesResults,
                             List<CompletedBillEvent> billEvents){
        this.parsedBill = billHtmlParser.getBill();
        this.checksum = billHtmlParser.getChecksum();
        this.sponsorNames = billHtmlParser.getSponsorNames();
        this.sponsorNames.completeAll(legislativeStructure);
        this.url = billHtmlParser.getUrl();
        this.votesResults = votesResults;
        this.billEvents = billEvents;
    }

    public BillIdentity getBillIdentity(){
        return parsedBill.getBillIdentity();
    }

    public List<CompletedBillEvent> getBillEvents(){
        return billEvents;
    }

    public List<CompletedBillEvent> getNonVoteBillEvents(){
        return Lists.filter(billEvents, event -> !event.isVote());
    }

    public List<CompletedBillEvent> getVoteEvents(){
        return Lists.filter(billEvents, CompletedBillEvent::isVote);
    }


    public List<BillEventKey> generateSearchedVoteLoadKeys(){
        return Collections.emptyList();
    }

    private List<CompletedBillEvent> findSponsorshipEvents(){
        return Lists.filter(billEvents, event -> BillActionType.isSponsoringEvent(event.getBillActionType()));
    }

    private List<CompletedBillEvent> findSponsorshipRemovalEvents(){
        return Lists.filter(billEvents,
                event -> event.getBillActionType().equals(BillActionType.REMOVE_SPONSOR)
                        || event.getBillActionType().equals(BillActionType.REMOVE_CHIEF_SPONSOR));
    }

    public Bill getParsedBill(){
        return parsedBill;
    }

    public SponsorNames getSponsorNames() {
        return sponsorNames;
    }

    public long getChecksum() {
        return checksum;
    }

    public String getUrl(){
        return url;
    }

    public BillVotesResults getBillVotesResults(CompletedBillEvent billEvent){
        BillVotesResults results = Lists.findfirst(votesResults, vr -> vr.matches(billEvent));
        if (results != null ){
            return results;
        } else {
            return BillVotesResults.EMPTY;
        }
    }

    public List<Name> getUncollatedVotes(){
        List<Name> uncollatedVotes = new ArrayList<>();
        for(BillVotesResults results : votesResults){
            uncollatedVotes.addAll(results.getUncollatedNames());
        }
        return uncollatedVotes;
    }

    public List<SponsorName> getUncollatedSponsors(){
        return sponsorNames.getUncollated();
    }

    public List<String> getErrors(){
        return new ArrayList<>(sponsorNames.findSponsorshipMismatches(findSponsorshipEvents(), findSponsorshipRemovalEvents()));
    }

}
