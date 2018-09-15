package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillActionLoad;
import org.center4racialjustice.legup.domain.Name;

import java.util.List;

public class BillSearchResults {

    private final BillVotesResults houseVoteResults;
    private final BillVotesResults senateVoteResults;
    private final Bill bill;
    private final SponsorNames sponsorNames;
    private final long checksum;
    private final String url;

    public BillSearchResults(){
        this.houseVoteResults = BillVotesResults.NO_RESULTS;
        this.senateVoteResults = BillVotesResults.NO_RESULTS;
        this.bill = null;
        this.sponsorNames = new SponsorNames();
        this.checksum = 0;
        this.url = null;
    }

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

    public BillSearchResults removeAlreadySavedData(List<BillActionLoad> existingDbLoads){
        BillActionLoads loads = new BillActionLoads(existingDbLoads);

        BillActionLoad mainLoad = loads.getBillHtmlLoad();
        boolean mainLoadMatches = mainLoad != null && mainLoad.matches(url, checksum);

        BillActionLoad houseVotesLoad = loads.getHouseVotesLoad();
        boolean houseLoadMatches = houseVotesLoad != null
                && houseVotesLoad.matches(houseVoteResults.getUrl(), houseVotesLoad.getCheckSum());

        BillActionLoad senateVotesLoad = loads.getSenateVotesLoad();
        boolean senateLoadMatches = senateVotesLoad != null
                && senateVotesLoad.matches(senateVotesLoad.getUrl(), senateVotesLoad.getCheckSum());


        return this;
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
