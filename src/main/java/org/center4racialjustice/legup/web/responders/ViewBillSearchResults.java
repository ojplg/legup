package org.center4racialjustice.legup.web.responders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.ConnectionWrapper;
import org.center4racialjustice.legup.domain.BillActionLoad;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.NameParser;
import org.center4racialjustice.legup.illinois.BillSearchResults;
import org.center4racialjustice.legup.illinois.BillSearcherParser;
import org.center4racialjustice.legup.illinois.SponsorNames;
import org.center4racialjustice.legup.service.BillPersistence;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSession;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;

import java.util.List;

public class ViewBillSearchResults implements Responder {

    private static final Logger log = LogManager.getLogger(ViewBillSearchResults.class);

    private final ConnectionPool connectionPool;
    private final BillPersistence billPersistence;
    private final NameParser nameParser;

    public ViewBillSearchResults(ConnectionPool connectionPool, NameParser nameParser) {
        this.connectionPool = connectionPool;
        this.billPersistence = new BillPersistence(connectionPool);
        this.nameParser =  nameParser;
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        Chamber chamber = submission.getConvertedParameter("chamber", Chamber.Converter);
        Long number = submission.getLongRequestParameter( "number");

        BillSearchResults billSearchResults = doSearch(chamber, number);
        if( BillSearchResults.MatchStatus.UnmatchedValues.equals(billSearchResults.getBillHtmlLoadStatus()) ){
            // Sometimes we get a mis-match that is erroneous.
            // I am not sure why. Perhaps the server is unreliable.
            // Perhaps there are problems with my local network or even
            // just the machine I am testing on. Very confusing.
            billSearchResults = doReSearch(chamber, number, billSearchResults);
        }

        String oneTimeKey = submission.setObject(LegupSession.BillSearchResultsKey, billSearchResults);

        LegupResponse response = new LegupResponse(this.getClass());

        response.putVelocityData("billSearchResults", billSearchResults);
        response.putVelocityData("bill", billSearchResults.getBill());

        boolean hasUncollatedVotes = billSearchResults.getUncollatedHouseVotes().size() > 0
                || billSearchResults.getUncollatedSenateVotes().size() > 0;
        response.putVelocityData("hasUncollatedVotes", hasUncollatedVotes);
        response.putVelocityData("uncollatedHouseVotes", billSearchResults.getUncollatedHouseVotes());
        response.putVelocityData("uncollatedSenateVotes", billSearchResults.getUncollatedSenateVotes());

        SponsorNames sponsorNames = billSearchResults.getSponsorNames();

        response.putVelocityData("uncollatedSponsors", sponsorNames.getUncollated());
        response.putVelocityData("hasUncollatedSponsors", sponsorNames.getUncollated().size() > 0);

        response.putVelocityData("chiefHouseSponsor", sponsorNames.getChiefHouseSponsor());
        response.putVelocityData("chiefSenateSponsor", sponsorNames.getChiefSenateSponsor());

        response.putVelocityData("houseSponsorCount", sponsorNames.getHouseSponsors().size());
        response.putVelocityData("senateSponsorCount", sponsorNames.getSenateSponsors().size());

        response.putVelocityData("houseVoteCount", billSearchResults.getHouseVotes().size());
        response.putVelocityData("senateVoteCount", billSearchResults.getSenateVotes().size());

        response.putVelocityData("DateTimeFormatter", BillActionLoad.Formatter);

        response.putVelocityData("oneTimeKey", oneTimeKey);

        return response;
    }

    private BillSearchResults doReSearch(Chamber chamber, Long number, BillSearchResults billSearchResults){
        BillActionLoad billActionLoad = billSearchResults.getBillHtmlLoad();
        StringBuilder buf = new StringBuilder();
        buf.append("Re-searching for bill html do to mismatch that could be erroneous. ");
        buf.append("Chamber: ");
        buf.append(chamber);
        buf.append(" Number: ");
        buf.append(number);
        buf.append(" Persisted checksum: ");
        buf.append(billActionLoad.getCheckSum());
        buf.append(" Persisted URL: ");
        buf.append(billActionLoad.getUrl());
        buf.append(" Persisted load time: ");
        buf.append(billActionLoad.getLoadTime());
        buf.append(" Current URL: ");
        buf.append(billSearchResults.getUrl());
        buf.append(" Current checksum: ");
        buf.append(billSearchResults.getChecksum());
        log.warn(buf.toString());

        BillSearchResults retriedBillSearchResults = doSearch(chamber, number);

        StringBuilder buf2 = new StringBuilder();
        buf2.append("Searched again for Chamber: ");
        buf2.append(chamber);
        buf2.append(" Number: ");
        buf2.append(number);
        buf2.append(" URL: ");
        buf2.append(retriedBillSearchResults.getUrl());
        buf2.append(" Checksum: ");
        buf2.append(retriedBillSearchResults.getChecksum());

        log.warn(buf2.toString());

        return retriedBillSearchResults;
    }

    private BillSearchResults doSearch(Chamber chamber, Long number){
        try (ConnectionWrapper connection = connectionPool.getWrappedConnection()) {
            BillSearcherParser billSearcherParser = new BillSearcherParser(connection, nameParser);
            BillSearchResults billSearchResults = billSearcherParser.doFullSearch(chamber, number);

            List<BillActionLoad> priorLoads = billPersistence.checkForPriorLoads(billSearchResults);
            log.info("Found prior loads for: " + chamber + "." + number + ": " + priorLoads);
            billSearchResults.setPriorLoads(priorLoads);
            return billSearchResults;
        }
    }
}
