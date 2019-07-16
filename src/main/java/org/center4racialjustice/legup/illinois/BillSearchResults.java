package org.center4racialjustice.legup.illinois;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillActionLoad;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.util.Lists;
import org.center4racialjustice.legup.util.Tuple;

import java.util.ArrayList;
import java.util.List;

public class BillSearchResults {

    private final Logger log = LogManager.getLogger(BillSearchResults.class);

    public enum MatchStatus {
        Unchecked,NoPriorValues,MatchedValues,UnmatchedValues;
    }

    private final Bill parsedBill;
    private final Bill savedBill;
    private final SponsorNames sponsorNames;
    private final long checksum;
    private final String url;
    private final List<BillVotesResults> votesResults;

    private final BillActionLoads billActionLoads;

    public BillSearchResults(BillHtmlParser billHtmlParser,
                             List<Legislator> legislators,
                             List<BillVotesResults> votesResults,
                             Tuple<Bill,List<BillActionLoad>> savedBillInformation){
        this.parsedBill = billHtmlParser.getBill();
        this.sponsorNames = billHtmlParser.getSponsorNames();
        this.sponsorNames.completeAll(legislators);
        this.checksum = billHtmlParser.getChecksum();
        this.url = billHtmlParser.getUrl();
        this.votesResults = votesResults;
        this.billActionLoads = new BillActionLoads(savedBillInformation.getSecond());
        this.savedBill = savedBillInformation.getFirst();
    }

    public MatchStatus getBillHtmlLoadStatus(){
        BillActionLoad mainLoad = billActionLoads.getBillHtmlLoad();
        if ( mainLoad == null ){
            return MatchStatus.NoPriorValues;
        } else if ( mainLoad.matches(url, checksum) ){
            return MatchStatus.MatchedValues;
        } else {
            log.warn("UNMATCHED. \nCurrent URL:\n" + url + "\nCurrent Checksum:\n" + checksum
                    + "\nOld URL:\n" + mainLoad.getUrl() + "\nOld Checksum:\n" + mainLoad.getCheckSum());
            return MatchStatus.UnmatchedValues;
        }
    }

    public List<String> generateSearchedVoteLoadKeys(){
        return Lists.map(votesResults, r -> r.generateKey(parsedBill));
    }

    public List<String> getPriorVoteLoadKeys(){
        return billActionLoads.getVoteLoadKeys();
    }

    public List<BillActionLoad> getPriorVoteLoads(){
        return billActionLoads.getVoteLoads();
    }

    public BillVotesResults getSearchedResults(String key){
        return Lists.findfirst(votesResults, res -> res.generateKey(parsedBill).equals(key));
    }

    public MatchStatus getVoteMatchStatus(String key){
        if( getPriorVoteLoadKeys().contains(key) ){
            BillActionLoad load = billActionLoads.getByKey(key);
            BillVotesResults results = getSearchedResults(key);
            if ( load.matches(results.getUrl(), results.getChecksum()) ){
                return MatchStatus.MatchedValues;
            } else {
                return MatchStatus.UnmatchedValues;
            }
        } else {
            return MatchStatus.NoPriorValues;
        }
    }

    public List<MatchStatus> allVoteMatchStatuses(){
        return Lists.map(generateSearchedVoteLoadKeys(), this::getVoteMatchStatus);
    }

    public BillActionLoad getBillHtmlLoad(){
        return billActionLoads.getBillHtmlLoad();
    }

    public Bill getParsedBill(){
        return parsedBill;
    }

    public Bill getSavedBill(){
        return savedBill;
    }

    public Bill getUpdatedBill(){
        savedBill.setShortDescription(parsedBill.getShortDescription());
        return savedBill;
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

    public BillActionLoad createVoteBillActionLoad(Bill bill, String key){
        BillVotesResults results = getSearchedResults(key);
        return BillActionLoad.create(bill, results.getUrl(), results.getChecksum());
    }

    public List<CollatedVote> getCollatedVotes(String key){
        BillVotesResults results = getSearchedResults(key);
        return results.getCollatedVotes();
    }

    public BillVotesResults getBillVotesResults(String key){
        return getSearchedResults(key);
    }

    public List<Name> getUncollatedVotes(){
        List<Name> uncollatedVotes = new ArrayList<>();
        for(BillVotesResults results : votesResults){
            uncollatedVotes.addAll(results.getUncollatedNames());
        }
        return uncollatedVotes;
    }

    public boolean hasDataToSave(){
        return getBillHtmlLoadStatus() != MatchStatus.MatchedValues
                || (! allVoteMatchStatuses().contains(MatchStatus.MatchedValues) );
    }
}
