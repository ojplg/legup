package org.center4racialjustice.legup.illinois;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.db.ConnectionWrapper;
import org.center4racialjustice.legup.db.LegislatorDao;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.domain.NameParser;

import java.util.List;
import java.util.Map;

public class BillSearcherParser {

    private final Logger log = LogManager.getLogger(BillSearcherParser.class);

    private final ConnectionWrapper connectionWrapper;
    private final NameParser nameParser;

    public BillSearcherParser(ConnectionWrapper connectionWrapper, NameParser nameParser){
        this.connectionWrapper = connectionWrapper;
        this.nameParser = nameParser;
    }

    public BillSearchResults doFullSearch(Chamber chamber, Long billNumber){
        log.info("Doing search for " + chamber + "." + billNumber);

        BillSearcher searcher = new BillSearcher();

        String billHomePageUrl = searcher.searchForBaseUrl(chamber, billNumber);
        String votesUrl = searcher.convertToVotesPage(billHomePageUrl);

        BillHtmlParser billHtmlParser = new BillHtmlParser(billHomePageUrl);

        log.info("Found " + billHtmlParser.getSponsorNames().totalSponsorCount() + " total sponsors");

        Map<String, String> votesUrlsMap = searcher.searchForVotesUrls(votesUrl);

        LegislatorDao legislatorDao = new LegislatorDao(connectionWrapper);
        List<Legislator> legislators = legislatorDao.readBySession(billHtmlParser.getSession());

        BillVotesResults houseVoteResults = findVotes(votesUrlsMap, legislators, Chamber.House);
        BillVotesResults senateVoteResults = findVotes(votesUrlsMap, legislators, Chamber.Senate);

        return new BillSearchResults(billHtmlParser, legislators, houseVoteResults, senateVoteResults);
    }

    private BillVotesResults findVotes(Map<String, String> votesMapUrl, List<Legislator> legislators, Chamber chamber) {
        String votePdfUrl = null;
        for( Map.Entry<String,String> urlPair : votesMapUrl.entrySet()){
            if( urlPair.getKey().contains("Third Reading")
                    && urlPair.getValue().contains(chamber.getName().toLowerCase())){
                votePdfUrl = urlPair.getValue();
                break;
            }
        }
        if( votePdfUrl == null ){
            log.info("No votes found in chamber " + chamber);
            return BillVotesResults.NO_RESULTS;
        }

        BillVotes billVotes = BillVotesParser.readFromUrlAndParse(votePdfUrl, nameParser);

        VotesLegislatorsCollator collator = new VotesLegislatorsCollator(legislators, billVotes);
        collator.collate();

        List<CollatedVote> collatedVotes = collator.getAllCollatedVotes();
        List<Name> uncollatedVotes = collator.getUncollated();

        log.info("Chamber " + chamber + " had " + collatedVotes.size() + " collated votes and " + uncollatedVotes + " uncollated");

        return new BillVotesResults(collatedVotes, uncollatedVotes, votePdfUrl, billVotes.getChecksum());
    }
}
