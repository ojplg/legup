package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.db.BillActionDao;
import org.center4racialjustice.legup.db.BillDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.BillActionSummary;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.VoteSide;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;

import java.util.List;

public class ViewBillVotes implements Responder {

    private final ConnectionPool connectionPool;

    public ViewBillVotes(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        long billId = submission.getLongRequestParameter("bill_id");

        return connectionPool.useConnection( connection -> {
            BillDao billDao = new BillDao(connection);

            Bill bill = billDao.read(billId);
            BillActionDao billActionDao = new BillActionDao(connection);

            List<BillAction> billActions = billActionDao.readByBill(bill);
            BillActionSummary billActionSummary = new BillActionSummary(billActions);

            HtmlLegupResponse legupResponse = new HtmlLegupResponse(this.getClass());

            legupResponse.putVelocityData("billActionSummary", billActionSummary);
            legupResponse.putVelocityData("bill", bill);

            legupResponse.putVelocityData("house", Chamber.House);
            legupResponse.putVelocityData("senate", Chamber.Senate);

            legupResponse.putVelocityData("yea", VoteSide.Yea);
            legupResponse.putVelocityData("nay", VoteSide.Nay);
            legupResponse.putVelocityData("notVoting", VoteSide.NotVoting);
            legupResponse.putVelocityData("present", VoteSide.Present);

            legupResponse.putVelocityData("sides", VoteSide.AllSides);

            return legupResponse;
        });
    }
}
