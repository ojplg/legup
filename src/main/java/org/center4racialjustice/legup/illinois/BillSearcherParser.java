package org.center4racialjustice.legup.illinois;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.LegislatorDao;
import org.center4racialjustice.legup.domain.BillEvent;
import org.center4racialjustice.legup.domain.BillEventData;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.domain.NameParser;
import org.center4racialjustice.legup.domain.VoteType;
import org.center4racialjustice.legup.util.Lists;
import org.center4racialjustice.legup.util.Tuple;

import java.util.ArrayList;
import java.util.List;

public class BillSearcherParser {

    private final Logger log = LogManager.getLogger(BillSearcherParser.class);

    private final ConnectionPool connectionPool;
    private final NameParser nameParser;

    public BillSearcherParser(ConnectionPool connectionPool, NameParser nameParser){
        this.connectionPool = connectionPool;
        this.nameParser = nameParser;
    }

    public BillSearchResults doFullSearch(LegislationType legislationType, Long billNumber){
        log.info("Doing search for " + legislationType + "." + billNumber);

        BillSearcher searcher = new BillSearcher();

        String billHomePageUrl = searcher.searchForBaseUrl(legislationType, billNumber);
        String votesUrl = searcher.convertToVotesPage(billHomePageUrl);

        BillHtmlParser billHtmlParser = new BillHtmlParser(billHomePageUrl);

        log.info("Found " + billHtmlParser.getSponsorNames().totalSponsorCount() + " total sponsors");

        List<VoteLinkInfo> votesLinks = searcher.searchForVoteLinks(votesUrl);

        List<Legislator> legislators = legislatorsBySession(billHtmlParser.getSession());

        List<BillEvent> billEvents = billHtmlParser.getBillEvents();
        BillEventParser billEventParser = new BillEventParser();
        List<BillEventData> eventDataList = Lists.map(billEvents, billEventParser::parse);

        List<BillVotesResults> votesResults = findVotes(votesLinks, legislators);

        return new BillSearchResults(billHtmlParser, legislators, votesResults, eventDataList);

    }

    private List<Legislator> legislatorsBySession(long sessionNumber){
        return connectionPool.useConnection(connection -> {
            LegislatorDao legislatorDao = new LegislatorDao(connection);
            return legislatorDao.readBySession(sessionNumber);
        });
    }

    private List<BillVotesResults> findVotes(List<VoteLinkInfo> votesLinks, List<Legislator> legislators) {
        List<BillVotesResults> votesList = new ArrayList<>();
        for( VoteLinkInfo voteLinkInfo : votesLinks ){
            BillVotesResults results = findVoteResults(voteLinkInfo, legislators);
            votesList.add(results);
        }
        return votesList;
    }

    private BillVotesResults findVoteResults(VoteLinkInfo voteLinkInfo, List<Legislator> legislators){
        BillVotes billVotes = BillVotesParser.readFromUrlAndParse(voteLinkInfo.getPdfUrl(), nameParser);
        Chamber votingChamber = billVotes.getVotingChamber();

        VotesLegislatorsCollator collator = new VotesLegislatorsCollator(legislators, billVotes);
        collator.collate();

        List<CollatedVote> collatedVotes = collator.getAllCollatedVotes();
        List<Name> uncollatedVotes = collator.getUncollated();

        log.info("Chamber " + votingChamber + " had " + collatedVotes.size() + " collated votes and " + uncollatedVotes + " uncollated");

        return new BillVotesResults(voteLinkInfo, collatedVotes, uncollatedVotes,  billVotes.getChecksum());
    }
}
