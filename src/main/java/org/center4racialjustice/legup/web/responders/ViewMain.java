package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.db.BillDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;

import java.util.List;

public class ViewMain implements Responder {

    private final ConnectionPool connectionPool;

    public ViewMain(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        return connectionPool.useConnection(connection ->  {
            BillDao billDao = new BillDao(connection);
            List<Long> billSessions = billDao.uniqueSessions();
            HtmlLegupResponse response = HtmlLegupResponse.withHelp(this.getClass(), submission.getLoggedInUser());
            response.putVelocityData("billSessions", billSessions);
            return response;
        });
    }

}
