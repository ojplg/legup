package org.center4racialjustice.legup.illinois;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.BillEvent;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Committee;
import org.center4racialjustice.legup.domain.CompletedBillEvent;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.domain.NameParser;
import org.center4racialjustice.legup.service.LegislativeStructure;
import org.center4racialjustice.legup.service.LegislatorPersistence;

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

        BillHtmlParser billHtmlParser = new BillHtmlParser(billHomePageUrl, nameParser);

        log.info("Found " + billHtmlParser.getSponsorNames().totalSponsorCount() + " total sponsors");

        List<VoteLinkInfo> votesLinks = searcher.searchForVoteLinks(votesUrl);

        LegislativeStructure legislativeStructure = sessionStructure(billHtmlParser.getSession());

        List<BillEvent> billEvents = billHtmlParser.getBillEvents();
        List<CompletedBillEvent> completedEvents = completeEvents(billEvents, legislativeStructure);

        List<BillVotesResults> votesResults = findVotes(votesLinks, legislativeStructure);
        votesResults.forEach(vr -> vr.getUncollatedNames().forEach(n -> log.warn("Could not collate " + n)));

        return new BillSearchResults(billHtmlParser, legislativeStructure, votesResults, completedEvents);

    }

    private List<CompletedBillEvent> completeEvents(List<BillEvent> billEvents, LegislativeStructure legislativeStructure){
        List<CompletedBillEvent> completedEventList = new ArrayList<>();
        for(BillEvent billEventData : billEvents){
            if( billEventData.hasLegislator() ){
                Legislator legislator = legislativeStructure.findLegislatorByMemberID(billEventData.getLegislatorMemberID());
                completedEventList.add(CompletedBillEvent.forLegislator(billEventData, legislator));
            } else if ( billEventData.hasCommittee() ){
                Committee committee = legislativeStructure.findCommitteeByCommitteeID(billEventData.getCommitteeID());
                completedEventList.add(CompletedBillEvent.forCommittee(billEventData, committee));
            } else {
                completedEventList.add(CompletedBillEvent.forStandalone(billEventData));
            }
        }
        return completedEventList;
    }

    private LegislativeStructure sessionStructure(long sessionNumber){
        LegislatorPersistence legislatorPersistence = new LegislatorPersistence(connectionPool);
        return legislatorPersistence.loadStructure(sessionNumber);
    }

    private List<BillVotesResults> findVotes(List<VoteLinkInfo> votesLinks, LegislativeStructure legislature) {
        List<BillVotesResults> votesList = new ArrayList<>();
        for( VoteLinkInfo voteLinkInfo : votesLinks ){
            BillVotesResults results = findVoteResults(voteLinkInfo, legislature);
            votesList.add(results);
        }
        return votesList;
    }

    private BillVotesResults findVoteResults(VoteLinkInfo voteLinkInfo, LegislativeStructure legislature){
        BillVotes billVotes = BillVotesParser.readFromUrlAndParse(voteLinkInfo.getPdfUrl(), nameParser);
        Chamber votingChamber = billVotes.getVotingChamber();

        VotesLegislatorsCollator collator = new VotesLegislatorsCollator(legislature, billVotes);
        collator.collate();

        List<CollatedVote> collatedVotes = collator.getAllCollatedVotes();
        List<Name> uncollatedVotes = collator.getUncollated();

        log.info("Chamber " + votingChamber + " had " + collatedVotes.size() + " collated votes and " + uncollatedVotes + " uncollated");

        return new BillVotesResults(voteLinkInfo, collatedVotes, uncollatedVotes,  billVotes.getChecksum());
    }
}
