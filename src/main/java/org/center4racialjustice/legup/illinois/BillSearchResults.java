package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.util.Tuple;

import java.util.List;

public class BillSearchResults {

    private final BillHtmlParser billHtmlParser;
    private final List<CollatedVote> houseVotes;
    private final List<Name> uncollatedHouseVotes;
    private final List<CollatedVote> senateVotes;
    private final List<Name> uncollatedSenateVotes;
    private final Bill bill;
    private final SponsorNames sponsorNames;
    private final long checksum;
    private final String url;

    public BillSearchResults(BillHtmlParser billHtmlParser,
                             Tuple<List<CollatedVote>, List<Name>> houseVotes,
                             Tuple<List<CollatedVote>, List<Name>> senateVotes){
        this.bill = billHtmlParser.getBill();
        this.sponsorNames = billHtmlParser.getSponsorNames();
        this.checksum = billHtmlParser.getChecksum();
        this.url = billHtmlParser.getUrl();
        this.billHtmlParser = billHtmlParser;
        this.houseVotes = houseVotes.getFirst();
        this.uncollatedHouseVotes = houseVotes.getSecond();
        this.senateVotes = senateVotes.getFirst();
        this.uncollatedSenateVotes = senateVotes.getSecond();
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
        return houseVotes;
    }

    public List<Name> getUncollatedHouseVotes() {
        return uncollatedHouseVotes;
    }

    public List<CollatedVote> getSenateVotes() {
        return senateVotes;
    }

    public List<Name> getUncollatedSenateVotes() {
        return uncollatedSenateVotes;
    }
}
