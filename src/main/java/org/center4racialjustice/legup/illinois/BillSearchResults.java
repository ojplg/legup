package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillActionLoad;
import org.center4racialjustice.legup.domain.BillEvent;
import org.center4racialjustice.legup.domain.BillEventKey;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.util.Lists;

import java.util.ArrayList;
import java.util.List;

public class BillSearchResults {

    public enum MatchStatus {
        Unchecked,NoPriorValues,MatchedValues,UnmatchedValues;
    }

    private final Bill parsedBill;
    private final SponsorNames sponsorNames;
    private final long checksum;
    private final String url;
    private final List<BillVotesResults> votesResults;
    private final List<BillEvent> billEvents;

    public BillSearchResults(BillHtmlParser billHtmlParser,
                             List<Legislator> legislators,
                             List<BillVotesResults> votesResults,
                             List<BillEvent> billEvents){
        this.parsedBill = billHtmlParser.getBill();
        this.sponsorNames = billHtmlParser.getSponsorNames();
        this.sponsorNames.completeAll(legislators);
        this.checksum = billHtmlParser.getChecksum();
        this.url = billHtmlParser.getUrl();
        this.votesResults = votesResults;
        this.billEvents = billEvents;
    }

    public BillIdentity getBillIdentity(){
        return parsedBill.getBillIdentity();
    }

    public List<BillEvent> getBillEvents(){
        return billEvents;
    }

    public List<BillEventKey> generateSearchedVoteLoadKeys(){
        return Lists.map(votesResults, vr -> vr.generateEventKey());
    }

    public BillVotesResults getSearchedResults(BillEventKey key){
        return Lists.findfirst(votesResults, res -> res.generateEventKey().equals(key));
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

    public BillActionLoad createVoteBillActionLoad(Bill bill, BillEventKey key){
        BillVotesResults results = getSearchedResults(key);
        return BillActionLoad.create(bill, results.getUrl(), results.getChecksum());
    }

    public List<CollatedVote> getCollatedVotes(BillEventKey key){
        BillVotesResults results = getSearchedResults(key);
        return results.getCollatedVotes();
    }

    public BillVotesResults getBillVotesResults(BillEventKey key){
        return getSearchedResults(key);
    }

    public List<Name> getUncollatedVotes(){
        List<Name> uncollatedVotes = new ArrayList<>();
        for(BillVotesResults results : votesResults){
            uncollatedVotes.addAll(results.getUncollatedNames());
        }
        return uncollatedVotes;
    }

}
