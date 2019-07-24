package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillHistory;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.service.BillPersistence;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.NavLink;
import org.center4racialjustice.legup.web.Responder;

import java.util.Arrays;
import java.util.List;

public class ViewBillSponsors implements Responder {

    private final BillPersistence billPersistence;

    public ViewBillSponsors(ConnectionPool connectionPool) {
        this.billPersistence = new BillPersistence(connectionPool);
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        long billId = submission.getLongRequestParameter("bill_id");

        BillHistory billHistory = billPersistence.loadBillHistory(billId);
        Bill bill = billHistory.getBill();

        HtmlLegupResponse response = HtmlLegupResponse.withLinks(this.getClass(),
                submission.getLoggedInUser(), navLinks(billId, bill.getSession()));

        response.putVelocityData("bill", bill);
        response.putVelocityData("billHistory", billHistory);

        response.putVelocityData("chambers", Chamber.ALL_CHAMBERS);

        return response;
    }

    private List<NavLink> navLinks(long billId, long sessionNumber){
        return Arrays.asList(
                new NavLink("Bills Index", "/legup/view_bills?session_number=" + sessionNumber),
                new NavLink("View Votes", "/legup/view_bill_votes?bill_id=" + billId)
        );
    }

}
