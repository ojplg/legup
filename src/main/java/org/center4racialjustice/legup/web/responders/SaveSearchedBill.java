package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.BillSaveResults;
import org.center4racialjustice.legup.illinois.BillSearchResults;
import org.center4racialjustice.legup.service.BillPersistence;
import org.center4racialjustice.legup.service.BillStatusComputer;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSession;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.NavLink;
import org.center4racialjustice.legup.web.Responder;

import java.util.Arrays;
import java.util.List;

public class SaveSearchedBill implements Responder {

    private final ConnectionPool connectionPool;

    public SaveSearchedBill(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        BillStatusComputer billStatusComputer = (BillStatusComputer) submission
                .getObject(LegupSession.BillStatusComputerKey);

        boolean forceSave = submission.getBooleanRequestParameter("force_save");

        BillPersistence billPersistence = new BillPersistence(connectionPool);
        BillSaveResults billSaveResults = billPersistence.saveParsedData(billStatusComputer, forceSave);

        long sessionNumber = billSaveResults.getBill().getSession();

        HtmlLegupResponse response = HtmlLegupResponse.withLinks(this.getClass(), navLinks(sessionNumber));
        response.putVelocityData("bill", billSaveResults.getBill());
        response.putVelocityData("billSaveResults", billSaveResults);
        return response;
    }

    public List<NavLink> navLinks(long sessionNumber) {
        return Arrays.asList(
            new NavLink("Bill Search", "/legup/view_bill_search_form"),
            new NavLink("Bills Index", "/legup/view_bills?session_number=" + sessionNumber)
        );
    }
}
