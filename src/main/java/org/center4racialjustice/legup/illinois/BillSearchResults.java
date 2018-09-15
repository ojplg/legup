package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillActionLoad;
import org.center4racialjustice.legup.domain.Name;

import java.util.List;

public class BillSearchResults {

    enum MatchStatus {
        Unchecked,NoPriorValues,MatchedValues,UnmatchedValues;
    }

    private final Bill bill;
    private final SponsorNames sponsorNames;
    private final long checksum;
    private final String url;
    private final BillVotesResults houseVoteResults;
    private final BillVotesResults senateVoteResults;

    private MatchStatus mainLoadStatus = MatchStatus.Unchecked;
    private MatchStatus houseVoteLoadStatus = MatchStatus.Unchecked;
    private MatchStatus senateVoteLoadStatus = MatchStatus.Unchecked;

    public BillSearchResults(BillHtmlParser billHtmlParser,
                             BillVotesResults houseVoteResults,
                             BillVotesResults senateVoteResults){
        this.bill = billHtmlParser.getBill();
        this.sponsorNames = billHtmlParser.getSponsorNames();
        this.checksum = billHtmlParser.getChecksum();
        this.url = billHtmlParser.getUrl();
        this.houseVoteResults = houseVoteResults;
        this.senateVoteResults = senateVoteResults;
    }

    public void checkPriorLoads(List<BillActionLoad> existingDbLoads){
        BillActionLoads loads = new BillActionLoads(existingDbLoads);

        BillActionLoad mainLoad = loads.getBillHtmlLoad();
        if ( mainLoad == null ){
            mainLoadStatus = MatchStatus.NoPriorValues;
        } else if ( mainLoad.matches(url, checksum) ){
            mainLoadStatus = MatchStatus.MatchedValues;
        } else {
            mainLoadStatus = MatchStatus.UnmatchedValues;
        }

        BillActionLoad houseVotesLoad = loads.getHouseVotesLoad();
        if ( houseVotesLoad == null ){
            houseVoteLoadStatus = MatchStatus.NoPriorValues;
        } else if ( houseVotesLoad.matches(houseVoteResults.getUrl(), houseVoteResults.getChecksum()) ){
            houseVoteLoadStatus = MatchStatus.MatchedValues;
        } else {
            houseVoteLoadStatus = MatchStatus.UnmatchedValues;
        }

        BillActionLoad senateVotesLoad = loads.getSenateVotesLoad();
        if ( senateVotesLoad == null ){
            senateVoteLoadStatus = MatchStatus.NoPriorValues;
        } else if ( senateVotesLoad.matches(senateVoteResults.getUrl(), senateVoteResults.getChecksum()) ){
            senateVoteLoadStatus = MatchStatus.MatchedValues;
        } else {
            senateVoteLoadStatus = MatchStatus.UnmatchedValues;
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
