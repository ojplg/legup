package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillHistory;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.VoteSide;
import org.center4racialjustice.legup.service.BillPersistence;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.NavLink;
import org.center4racialjustice.legup.web.Responder;

import java.util.Arrays;
import java.util.List;

public class ViewBillVotes implements Responder {

    private final BillPersistence billPersistence;

    public ViewBillVotes(ConnectionPool connectionPool) {
        this.billPersistence = new BillPersistence(connectionPool);
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        long billId = submission.getLongRequestParameter("bill_id");
        BillHistory billHistory = billPersistence.loadBillHistory(billId);
        Bill bill = billHistory.getBill();

        HtmlLegupResponse legupResponse = HtmlLegupResponse.withLinks(this.getClass(),
                submission.getLoggedInUser(), navLinks(billId, bill.getSession()));

        legupResponse.putVelocityData("billHistory", billHistory);
        legupResponse.putVelocityData("bill", bill);
        legupResponse.putVelocityData("sides", VoteSide.AllSides);

        legupResponse.putVelocityData("house", Chamber.House);
        legupResponse.putVelocityData("senate", Chamber.Senate);
//
//            legupResponse.putVelocityData("yea", VoteSide.Yea);
//            legupResponse.putVelocityData("nay", VoteSide.Nay);
//            legupResponse.putVelocityData("notVoting", VoteSide.NotVoting);
//            legupResponse.putVelocityData("present", VoteSide.Present);
//            legupResponse.putVelocityData("absent", VoteSide.Absent);
//            legupResponse.putVelocityData("excused", VoteSide.Excused);
//

        return legupResponse;
    }

    private List<NavLink> navLinks(long billId, long sessionNumber){
        return Arrays.asList(
                new NavLink("Bills Index", "/legup/view_bills?session_number=" +sessionNumber),
                new NavLink("View Sponsors", "/legup/view_bill_sponsors?bill_id=" + billId)
        );
    }

}
