package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.service.BillPersistence;
import org.center4racialjustice.legup.util.LookupTable;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;

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

        HtmlLegupResponse response = HtmlLegupResponse.simpleResponse(this.getClass(), submission.getLoggedInUser());
        response.putVelocityData("billActionTable", billActionTable);
        response.putVelocityData("legislators", billActionTable.sortedRowHeadings(Legislator::compareTo));
        return response;
    }
}
