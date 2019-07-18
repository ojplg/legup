package org.center4racialjustice.legup.illinois;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.LegislatorDao;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillActionLoad;
import org.center4racialjustice.legup.domain.BillEvent;
import org.center4racialjustice.legup.domain.BillEventData;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.domain.NameParser;
import org.center4racialjustice.legup.domain.VoteType;
import org.center4racialjustice.legup.service.BillPersistence;
import org.center4racialjustice.legup.util.Lists;
import org.center4racialjustice.legup.util.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        Map<String, String> votesUrlsMap = searcher.searchForVotesUrls(votesUrl);

        List<Legislator> legislators = legislatorsBySession(billHtmlParser.getSession());

        List<BillEvent> billEvents = billHtmlParser.getBillEvents();
        BillEventParser billEventParser = new BillEventParser();
        List<Tuple<BillEvent, BillEventData>> eventTuples =
                Lists.map(billEvents, event -> new Tuple(event, billEventParser.parse(event)));
        ParsedBillEvents parsedBillEvents = new ParsedBillEvents(eventTuples);

        List<BillVotesResults> votesResults = findVotes(votesUrlsMap, legislators, parsedBillEvents);

        return new BillSearchResults(billHtmlParser, legislators, votesResults, parsedBillEvents);

    }

    private List<Legislator> legislatorsBySession(long sessionNumber){
        return connectionPool.useConnection(connection -> {
            LegislatorDao legislatorDao = new LegislatorDao(connection);
            return legislatorDao.readBySession(sessionNumber);
        });
    }

    private List<BillVotesResults> findVotes(Map<String, String> votesMapUrl, List<Legislator> legislators, ParsedBillEvents billEvents) {
        List<BillVotesResults> votesList = new ArrayList<>();
        for( Map.Entry<String,String> urlPair : votesMapUrl.entrySet() ){
            // TODO: THIS NEEDS FIXING
            BillEvent billEvent =  null; // Lists.findfirst(billEvents, event -> urlPair.getValue().equals(event.getLink()));
            BillVotesResults results = findVoteResults(urlPair.getKey(), urlPair.getValue(), legislators, billEvent);
            votesList.add(results);
        }
        return votesList;
    }

    private BillVotesResults findVoteResults(String linkText, String linkUrl, List<Legislator> legislators, BillEvent billEvent){
        VoteType voteType = new VoteType(linkText);
        BillVotes billVotes = BillVotesParser.readFromUrlAndParse(linkUrl, nameParser, voteType);
        Chamber votingChamber = billVotes.getVotingChamber();

        VotesLegislatorsCollator collator = new VotesLegislatorsCollator(legislators, billVotes);
        collator.collate();

        List<CollatedVote> collatedVotes = collator.getAllCollatedVotes();
        List<Name> uncollatedVotes = collator.getUncollated();

        log.info("Chamber " + votingChamber + " had " + collatedVotes.size() + " collated votes and " + uncollatedVotes + " uncollated");

        return new BillVotesResults(collatedVotes, uncollatedVotes, linkUrl, billVotes.getChecksum(), votingChamber, voteType, billEvent.getDate());
    }
}
