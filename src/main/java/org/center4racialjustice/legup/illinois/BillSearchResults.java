package org.center4racialjustice.legup.illinois;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillActionLoad;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.util.Tuple;

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
    private final BillVotesResults houseVoteResults;
    private final BillVotesResults senateVoteResults;

    private final BillActionLoads billActionLoads;

    public BillSearchResults(BillHtmlParser billHtmlParser,
                             List<Legislator> legislators,
                             BillVotesResults houseVoteResults,
                             BillVotesResults senateVoteResults,
                             Tuple<Bill,List<BillActionLoad>> savedBillInformation){
        this.parsedBill = billHtmlParser.getBill();
        this.sponsorNames = billHtmlParser.getSponsorNames();
        this.sponsorNames.completeAll(legislators);
        this.checksum = billHtmlParser.getChecksum();
        this.url = billHtmlParser.getUrl();
        this.houseVoteResults = houseVoteResults;
        this.senateVoteResults = senateVoteResults;
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

    public MatchStatus getHouseVotesLoadStatus(){
        if (billActionLoads == null ){
            return MatchStatus.Unchecked;
        }
        BillActionLoad houseVotesLoad = billActionLoads.getHouseVotesLoad();
        if ( houseVotesLoad == null ){
            return MatchStatus.NoPriorValues;
        } else if ( houseVotesLoad.matches(houseVoteResults.getUrl(), houseVoteResults.getChecksum()) ){
            return MatchStatus.MatchedValues;
        } else {
            return MatchStatus.UnmatchedValues;
        }
    }

    public MatchStatus getSenateVotesLoadStatus(){
        if (billActionLoads == null ){
            return MatchStatus.Unchecked;
        }
        BillActionLoad senateVotesLoad = billActionLoads.getSenateVotesLoad();
        if ( senateVotesLoad == null ){
            return MatchStatus.NoPriorValues;
        } else if ( senateVotesLoad.matches(senateVoteResults.getUrl(), senateVoteResults.getChecksum()) ){
            return MatchStatus.MatchedValues;
        } else {
            return MatchStatus.UnmatchedValues;
        }
    }

    public BillActionLoad getBillHtmlLoad(){
        if (billActionLoads == null ){
            return null;
        }
        return billActionLoads.getBillHtmlLoad();
    }

    public BillActionLoad getHouseVotesLoad(){
        if (billActionLoads == null ){
            return null;
        }
        return billActionLoads.getHouseVotesLoad();
    }

    public BillActionLoad getSenateVotesLoad(){
        if (billActionLoads == null ){
            return null;
        }
        return billActionLoads.getSenateVotesLoad();
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

    public BillActionLoad createHouseBillActionLoad(Bill bill){
        return BillActionLoad.create(bill, houseVoteResults.getUrl(), houseVoteResults.getChecksum());
    }

    public BillActionLoad createSenateBillActionLoad(Bill bill){
        return BillActionLoad.create(bill, senateVoteResults.getUrl(), senateVoteResults.getChecksum());
    }

    public List<CollatedVote> getHouseVotes() {
        return houseVoteResults.getCollatedVotes();
    }

    public List<Name> getUncollatedHouseVotes() {
        return houseVoteResults.getUncollatedNames();
    }

    public List<CollatedVote> getSenateVotes() {
        return senateVoteResults.getCollatedVotes();
    }

    public List<Name> getUncollatedSenateVotes() {
        return senateVoteResults.getUncollatedNames();
    }

    public boolean hasDataToSave(){
        return ! (MatchStatus.MatchedValues.equals(getBillHtmlLoadStatus())
                    && MatchStatus.MatchedValues.equals(getHouseVotesLoadStatus())
                    && MatchStatus.MatchedValues.equals(getSenateVotesLoadStatus()));
    }
}
