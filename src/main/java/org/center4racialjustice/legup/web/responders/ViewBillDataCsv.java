package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;

public class ViewBillDataCsv implements Responder {

    private final ViewBillDataTable innerResponder;

    public ViewBillDataCsv(ConnectionPool connectionPool){
        this.innerResponder = new ViewBillDataTable(connectionPool);
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        return innerResponder.handle(submission);
    }
}
