package org.center4racialjustice.legup.web.handlers;

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
import org.center4racialjustice.legup.service.BillPersistence;
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
        long billId = submission.getLongRequestParameter("bill_id");

        BillPersistence billPersistence = new BillPersistence(connectionPool);
        LookupTable<Legislator, String, String> billActionTable = billPersistence.generateBillActionSummary(billId);

        LegupResponse response = new LegupResponse(this.getClass());
        response.putVelocityData("billActionTable", billActionTable);
        response.putVelocityData("legislators", billActionTable.sortedRowHeadings(Legislator::compareTo));
        return response;
    }
}
