package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.db.ConnectionWrapper;
import org.center4racialjustice.legup.db.LegislatorDao;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Name;

import java.io.IOException;
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

            BillVotesResults houseVoteResults = findVotes(votesUrlsMap, legislators, Chamber.House);
            BillVotesResults senateVoteResults = findVotes(votesUrlsMap, legislators, Chamber.Senate);

            return new BillSearchResults(billHtmlParser, houseVoteResults, senateVoteResults);
        } catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }

    private BillVotesResults findVotes(Map<String, String> votesMapUrl, List<Legislator> legislators, Chamber chamber) throws IOException {
        String votePdfUrl = null;
        for( Map.Entry<String,String> urlPair : votesMapUrl.entrySet()){
            if( urlPair.getKey().contains("Third Reading")
                    && urlPair.getValue().contains(chamber.getName().toLowerCase())){
                votePdfUrl = urlPair.getValue();
                break;
            }
        }
        if( votePdfUrl == null ){
            return BillVotesResults.NO_RESULTS;
        }

        BillVotes billVotes = BillVotesParser.readFromUrlAndParse(votePdfUrl);

        VotesLegislatorsCollator collator = new VotesLegislatorsCollator(legislators, billVotes);
        collator.collate();

        List<CollatedVote> collatedVotes = collator.getAllCollatedVotes();
        List<Name> uncollatedVotes = collator.getUncollated();

        return new BillVotesResults(collatedVotes, uncollatedVotes, votePdfUrl, billVotes.getChecksum());
    }
}
