package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.DaoBuilders;
import org.center4racialjustice.legup.db.LegislatorDao;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Committee;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;
import org.hrorm.Dao;

import java.util.Collections;
import java.util.List;

import static org.hrorm.Operator.EQUALS;
import static org.hrorm.Where.where;

public class ViewCommittees implements Responder {

    private final ConnectionPool connectionPool;

    public ViewCommittees(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        return connectionPool.useConnection(connection ->  {
            long sessionNumber = submission.getLongRequestParameter("session_number");
            String chamber = submission.getParameter("chamber");

            Dao<Committee> committeeDao = DaoBuilders.COMMITTEE.buildDao(connection);

            List<Committee> committees = committeeDao.select(
                    where("session_number", EQUALS, sessionNumber)
                    .and("chamber", EQUALS, chamber));

            HtmlLegupResponse response = HtmlLegupResponse.simpleResponse(this.getClass(), submission.getLoggedInUser());
            response.putVelocityData("committees", committees);
            response.putVelocityData("chamber", Chamber.fromString(chamber));
            return response;
        });
    }
}
