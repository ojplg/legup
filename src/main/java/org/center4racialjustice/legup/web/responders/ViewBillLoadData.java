package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.db.BillActionLoadDao;
import org.center4racialjustice.legup.db.BillDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillActionLoad;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.NavLink;
import org.center4racialjustice.legup.web.Responder;

import java.util.Arrays;
import java.util.List;

public class ViewBillLoadData implements Responder {

    private final ConnectionPool connectionPool;

    public ViewBillLoadData(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        long billId = submission.getLongRequestParameter("bill_id");

        return connectionPool.useConnection( connection -> {
            BillDao billDao = new BillDao(connection);
            BillActionLoadDao loadDao = new BillActionLoadDao(connection);

            Bill bill = billDao.read(billId);
            List<BillActionLoad> loads = loadDao.readByBill(bill);

            HtmlLegupResponse legupResponse = HtmlLegupResponse.withLinks(this.getClass(),
                    submission.getLoggedInUser(), navLinks(billId, bill.getSession()));

            legupResponse.putVelocityData("bill", bill);
            legupResponse.putVelocityData("loads", loads);

            return legupResponse;
        });
    }

    private List<NavLink> navLinks(long billId, long sessionNumber){
        return Arrays.asList(
                new NavLink("Bills Index", "/legup/view_bills?session_number=" + sessionNumber),
                new NavLink("View Votes", "/legup/view_bill_votes?bill_id=" + billId),
                new NavLink("View Sponsors", "/legup/view_bill_sponsors?bill_id=" + billId)
        );
    }

}
