package org.center4racialjustice.legup.web.responders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.BillActionLoad;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.NameParser;
import org.center4racialjustice.legup.illinois.BillSearchResults;
import org.center4racialjustice.legup.illinois.BillSearcherParser;
import org.center4racialjustice.legup.illinois.LegislationType;
import org.center4racialjustice.legup.illinois.SponsorNames;
import org.center4racialjustice.legup.service.BillPersistence;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSession;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.NavLink;
import org.center4racialjustice.legup.web.Responder;

import java.util.Arrays;
import java.util.Collections;
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
        if ( ! submission.isValidLongParameter("number") ){
            return HtmlLegupResponse.forError(ViewBillSearchForm.class,
                    submission.getLoggedInUser(),
                    "Problem parsing form input",
                    Collections.singletonMap("number","Could not parse bill number from input " + submission.getParameter("number")));
        }

        String legislationSubType = submission.getParameter("legislation_sub_type");
        Chamber chamber = submission.getConvertedParameter("chamber", Chamber.Converter);

        LegislationType legislationType = LegislationType.fromChamberAndSubType(chamber, legislationSubType);
        Long number = submission.getLongRequestParameter( "number");

        BillSearchResults billSearchResults = doSearch(legislationType, number);
        if( BillSearchResults.MatchStatus.UnmatchedValues.equals(billSearchResults.getBillHtmlLoadStatus()) ){
            // Sometimes we get a mis-match that is erroneous.
            // I am not sure why. Perhaps the server is unreliable.
            // Perhaps there are problems with my local network or even
            // just the machine I am testing on. Very confusing.
            billSearchResults = doReSearch(legislationType, number, billSearchResults);
        }

        String oneTimeKey = submission.setObject(LegupSession.BillSearchResultsKey, billSearchResults);

        HtmlLegupResponse response = HtmlLegupResponse.withLinks(this.getClass(), submission.getLoggedInUser(), navLinks());

        response.putVelocityData("billSearchResults", billSearchResults);
        response.putVelocityData("bill", billSearchResults.getParsedBill());

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

    private BillSearchResults doReSearch(LegislationType legislationType, Long number, BillSearchResults billSearchResults){
        BillActionLoad billActionLoad = billSearchResults.getBillHtmlLoad();
        StringBuilder buf = new StringBuilder();
        buf.append("Re-searching for bill html do to mismatch that could be erroneous. ");
        buf.append("Chamber: ");
        buf.append(legislationType);
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

        BillSearchResults retriedBillSearchResults = doSearch(legislationType, number);

        StringBuilder buf2 = new StringBuilder();
        buf2.append("Searched again for Chamber: ");
        buf2.append(legislationType);
        buf2.append(" Number: ");
        buf2.append(number);
        buf2.append(" URL: ");
        buf2.append(retriedBillSearchResults.getUrl());
        buf2.append(" Checksum: ");
        buf2.append(retriedBillSearchResults.getChecksum());

        log.warn(buf2.toString());

        return retriedBillSearchResults;
    }

    private BillSearchResults doSearch(LegislationType legislationType, Long number){
        BillSearcherParser billSearcherParser = new BillSearcherParser(connectionPool, nameParser);
        BillSearchResults billSearchResults = billSearcherParser.doFullSearch(legislationType, number);
        return billSearchResults;
    }

    private List<NavLink> navLinks(){
        return Arrays.asList(
                new NavLink("Bill Search", "/legup/view_bill_search_form"),
                new NavLink("Bills Index", "/legup/view_bills")
        );
    }
}
