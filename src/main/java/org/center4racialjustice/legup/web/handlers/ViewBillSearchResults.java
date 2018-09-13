package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.illinois.BillHtmlParser;
import org.center4racialjustice.legup.illinois.BillSearchResults;
import org.center4racialjustice.legup.illinois.BillSearcherParser;
import org.center4racialjustice.legup.illinois.SponsorNames;
import org.center4racialjustice.legup.web.Handler;
import org.center4racialjustice.legup.web.LegupSession;
import org.center4racialjustice.legup.web.Util;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;

public class ViewBillSearchResults implements Handler {

    private final ConnectionPool connectionPool;

    public ViewBillSearchResults(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public VelocityContext handle(Request request, LegupSession legupSession, HttpServletResponse httpServletResponse) {

        Chamber chamber = Util.getConvertedParameter(request, "chamber", Chamber.Converter);
        Long number = Util.getLongParameter(request, "number");

        BillSearcherParser billSearcherParser = new BillSearcherParser(connectionPool.getWrappedConnection());
        BillSearchResults billSearchResults = billSearcherParser.doFullSearch(chamber, number);

        String oneTimeKey = legupSession.setObject(LegupSession.BillSearchResultsKey, billSearchResults);

        BillHtmlParser billHtmlParser = billSearchResults.getBillHtmlParser();

        VelocityContext velocityContext = new VelocityContext();

        velocityContext.put("bill", billHtmlParser.getBill());

        SponsorNames sponsorNames =  billHtmlParser.getSponsorNames();

        velocityContext.put("chief_house_sponsor", sponsorNames.getHouseChiefSponsor().getFirst());
        velocityContext.put("chief_senate_sponsor", sponsorNames.getSenateChiefSponsor().getFirst());
        velocityContext.put("house_sponsors", sponsorNames.getHouseSponsors());
        velocityContext.put("senate_sponsors", sponsorNames.getSenateSponsors());

        velocityContext.put("uncollatedHouseVotes", billSearchResults.getUncollatedHouseVotes());
        velocityContext.put("uncollatedSenateVotes", billSearchResults.getUncollatedSenateVotes());

        velocityContext.put("oneTimeKey", oneTimeKey);

        return velocityContext;
    }


}
