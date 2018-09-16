package org.center4racialjustice.legup.web.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.ConnectionWrapper;
import org.center4racialjustice.legup.domain.BillActionLoad;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.illinois.BillSearchResults;
import org.center4racialjustice.legup.illinois.BillSearcherParser;
import org.center4racialjustice.legup.illinois.SponsorNames;
import org.center4racialjustice.legup.service.BillPersistence;
import org.center4racialjustice.legup.web.Handler;
import org.center4racialjustice.legup.web.LegupSession;
import org.center4racialjustice.legup.web.Util;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class ViewBillSearchResults implements Handler {

    private static final Logger log = LogManager.getLogger(ViewBillSearchResults.class);

    private final ConnectionPool connectionPool;
    private final BillPersistence billPersistence;

    public ViewBillSearchResults(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.billPersistence = new BillPersistence(connectionPool);
    }

    @Override
    public VelocityContext handle(Request request, LegupSession legupSession, HttpServletResponse httpServletResponse) {

        Chamber chamber = Util.getConvertedParameter(request, "chamber", Chamber.Converter);
        Long number = Util.getLongParameter(request, "number");

        BillSearchResults billSearchResults = doSearch(chamber, number);
        if( BillSearchResults.MatchStatus.UnmatchedValues.equals(billSearchResults.getBillHtmlLoadStatus()) ){
            // Sometimes we get a mis-match that is erroneous.
            // I am not sure why. Perhaps the server is unreliable.
            // Perhaps there are problems with my local network or even
            // just the machine I am testing on. Very confusing.
            billSearchResults = doReSearch(chamber, number, billSearchResults);
        }

        String oneTimeKey = legupSession.setObject(LegupSession.BillSearchResultsKey, billSearchResults);

        VelocityContext velocityContext = new VelocityContext();

        velocityContext.put("billSearchResults", billSearchResults);
        velocityContext.put("bill", billSearchResults.getBill());

        boolean hasUncollatedVotes = billSearchResults.getUncollatedHouseVotes().size() > 0
                || billSearchResults.getUncollatedSenateVotes().size() > 0;
        velocityContext.put("hasUncollatedVotes", hasUncollatedVotes);
        velocityContext.put("uncollatedHouseVotes", billSearchResults.getUncollatedHouseVotes());
        velocityContext.put("uncollatedSenateVotes", billSearchResults.getUncollatedSenateVotes());

        SponsorNames sponsorNames = billSearchResults.getSponsorNames();

        velocityContext.put("uncollatedSponsors", sponsorNames.getUncollated());
        velocityContext.put("hasUncollatedSponsors", sponsorNames.getUncollated().size() > 0);

        velocityContext.put("chiefHouseSponsor", sponsorNames.getChiefHouseSponsor());
        velocityContext.put("chiefSenateSponsor", sponsorNames.getChiefSenateSponsor());

        velocityContext.put("houseSponsorCount", sponsorNames.getHouseSponsors().size());
        velocityContext.put("senateSponsorCount", sponsorNames.getSenateSponsors().size());

        velocityContext.put("houseVoteCount", billSearchResults.getHouseVotes().size());
        velocityContext.put("senateVoteCount", billSearchResults.getSenateVotes().size());

        velocityContext.put("DateTimeFormatter", BillActionLoad.Formatter);

        velocityContext.put("oneTimeKey", oneTimeKey);

        return velocityContext;

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
            BillSearcherParser billSearcherParser = new BillSearcherParser(connection);
            BillSearchResults billSearchResults = billSearcherParser.doFullSearch(chamber, number);

            List<BillActionLoad> priorLoads = billPersistence.checkForPriorLoads(billSearchResults);
            billSearchResults.setPriorLoads(priorLoads);
            return billSearchResults;
        }
    }
}
