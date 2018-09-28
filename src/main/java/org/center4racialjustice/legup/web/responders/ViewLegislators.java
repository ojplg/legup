package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.ConnectionWrapper;
import org.center4racialjustice.legup.db.LegislatorDao;
import org.center4racialjustice.legup.domain.Legislator;
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
        try (ConnectionWrapper connection = connectionPool.getWrappedConnection()) {
            LegislatorDao dao = new LegislatorDao(connection);
            List<Legislator> legislators = dao.readAll();
            Collections.sort(legislators);

            LegupResponse response = new LegupResponse(this.getClass());
            response.putVelocityData("legislators", legislators);
            return response;
        }
    }
}
