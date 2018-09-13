package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.util.Tuple;

import java.util.List;

public class BillSearchResults {

    private final BillHtmlParser billHtmlParser;
    private final List<CollatedVote> houseVotes;
    private final List<Name> uncollatedHouseVotes;
    private final List<CollatedVote> senateVotes;
    private final List<Name> uncollatedSenateVotes;

    public BillSearchResults(BillHtmlParser billHtmlParser,
                             Tuple<List<CollatedVote>, List<Name>> houseVotes,
                             Tuple<List<CollatedVote>, List<Name>> senateVotes){
        this.billHtmlParser = billHtmlParser;
        this.houseVotes = houseVotes.getFirst();
        this.uncollatedHouseVotes = houseVotes.getSecond();
        this.senateVotes = senateVotes.getFirst();
        this.uncollatedSenateVotes = senateVotes.getSecond();
    }

    public BillHtmlParser getBillHtmlParser() {
        return billHtmlParser;
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
