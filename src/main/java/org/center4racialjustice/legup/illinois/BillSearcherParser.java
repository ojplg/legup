package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.db.ConnectionWrapper;
import org.center4racialjustice.legup.db.LegislatorDao;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.util.Tuple;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BillSearcherParser {

    private final ConnectionWrapper connectionWrapper;

    public BillSearcherParser(ConnectionWrapper connectionWrapper){
        this.connectionWrapper = connectionWrapper;
    }

    public BillSearchResults doFullSearch(Chamber chamber, Long billNumber){

        try {
            BillSearcher searcher = new BillSearcher();

            String billHomePageUrl = searcher.searchForBaseUrl(chamber, billNumber);
            String votesUrl = searcher.convertToVotesPage(billHomePageUrl);

            BillHtmlParser billHtmlParser = new BillHtmlParser(billHomePageUrl);
            Map<String, String> votesUrlsMap = searcher.searchForVotesUrls(votesUrl);

            LegislatorDao legislatorDao = new LegislatorDao(connectionWrapper);
            List<Legislator> legislators = legislatorDao.readBySession(billHtmlParser.getSession());

            Tuple<List<CollatedVote>, List<Name>> houseVoteResults = findVotes(votesUrlsMap, legislators, Chamber.House);
            Tuple<List<CollatedVote>, List<Name>> senateVoteResults = findVotes(votesUrlsMap, legislators, Chamber.Senate);

            return new BillSearchResults(billHtmlParser, houseVoteResults, senateVoteResults);
        } catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }

    private Tuple<List<CollatedVote>, List<Name>> findVotes(Map<String, String> votesMapUrl, List<Legislator> legislators, Chamber chamber) throws IOException {
        String votePdfUrl = null;
        for( Map.Entry<String,String> urlPair : votesMapUrl.entrySet()){
            if( urlPair.getKey().contains("Third Reading")
                    && urlPair.getValue().contains(chamber.getName().toLowerCase())){
                votePdfUrl = urlPair.getValue();
                break;
            }
        }
        if( votePdfUrl == null ){
            return new Tuple(Collections.emptyList(), Collections.emptyList());
        }

        BillVotes billVotes = BillVotesParser.readFromUrlAndParse(votePdfUrl);

        VotesLegislatorsCollator collator = new VotesLegislatorsCollator(legislators, billVotes);
        collator.collate();

        List<CollatedVote> collatedVotes = collator.getAllCollatedVotes();
        List<Name> uncollatedVotes = collator.getUncollated();

        return new Tuple(collatedVotes, uncollatedVotes);
    }
}
