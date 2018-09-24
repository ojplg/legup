package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.db.BillActionDao;
import org.center4racialjustice.legup.db.BillDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.ConnectionWrapper;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.BillActionSummary;
import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Vote;
import org.center4racialjustice.legup.util.LookupTable;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;

import java.util.List;

public class ViewBillDataTable implements Responder {

    private final ConnectionPool connectionPool;

    public ViewBillDataTable(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {

        try (ConnectionWrapper connection = connectionPool.getWrappedConnection()) {
            long billId = submission.getLongRequestParameter("bill_id");

            BillDao billDao = new BillDao(connection);
            BillActionDao billActionDao = new BillActionDao(connection);

            Bill bill = billDao.read(billId);

            List<BillAction> billActions = billActionDao.readByBill(bill);

            LookupTable<Legislator, String, String> billActionTable = new LookupTable<>();

            for( BillAction billAction : billActions ){
                Legislator leg = billAction.getLegislator();
                if ( billAction.isVote() ){
                    Vote vote = billAction.asVote();
                    billActionTable.put(leg, "Vote", vote.getVoteSide().getDisplayString());
                } else {
                    billActionTable.put(leg, billAction.getBillActionType().getCode(), "Check");
                }
            }

            LegupResponse response = new LegupResponse();
            response.putVelocityData("billActionTable", billActionTable);
            response.putVelocityData("legislators", billActionTable.sortedRowHeadings(Legislator::compareTo));
            return response;
        }
    }
}
