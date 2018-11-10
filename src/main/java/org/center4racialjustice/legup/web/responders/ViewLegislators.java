package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.LegislatorDao;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;

import java.util.Collections;
import java.util.List;

public class ViewLegislators implements Responder {

    private final ConnectionPool connectionPool;

    public ViewLegislators(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        return connectionPool.useConnection(connection ->  {
            LegislatorDao dao = new LegislatorDao(connection);
            List<Legislator> legislators = dao.readAll();
            Collections.sort(legislators);

            HtmlLegupResponse response = HtmlLegupResponse.simpleResponse(this.getClass(), submission.getLoggedInUser());
            response.putVelocityData("legislators", legislators);
            return response;
        });
    }
}
