package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillActionLoad;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Name;

import java.util.List;

public class BillSearchResults {

    public enum MatchStatus {
        Unchecked,NoPriorValues,MatchedValues,UnmatchedValues;
    }

    private final Bill bill;
    private final SponsorNames sponsorNames;
    private final long checksum;
    private final String url;
    private final BillVotesResults houseVoteResults;
    private final BillVotesResults senateVoteResults;

    private BillActionLoads billActionLoads = null;

    public BillSearchResults(BillHtmlParser billHtmlParser,
                             List<Legislator> legislators,
                             BillVotesResults houseVoteResults,
                             BillVotesResults senateVoteResults){
        this.bill = billHtmlParser.getBill();
        this.sponsorNames = billHtmlParser.getSponsorNames();
        this.sponsorNames.completeAll(legislators);
        this.checksum = billHtmlParser.getChecksum();
        this.url = billHtmlParser.getUrl();
        this.houseVoteResults = houseVoteResults;
        this.senateVoteResults = senateVoteResults;
    }

    public void setPriorLoads(List<BillActionLoad> existingDbLoads){
        billActionLoads = new BillActionLoads(existingDbLoads);
    }

    public MatchStatus getMainLoadStatus(){
        BillActionLoad mainLoad = billActionLoads.getBillHtmlLoad();
        if ( mainLoad == null ){
            return MatchStatus.NoPriorValues;
        } else if ( mainLoad.matches(url, checksum) ){
            return MatchStatus.MatchedValues;
        } else {
            return MatchStatus.UnmatchedValues;
        }
    }

    public MatchStatus getHouseVoteLoadStatus(){
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

    public MatchStatus getSenateVoteLoadStatus(){
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

    public Bill getBill(){
        return bill;
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

}
