package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.DaoBuilders;
import org.center4racialjustice.legup.domain.Committee;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;
import org.hrorm.Dao;

public class ViewCommittee implements Responder {

    private final ConnectionPool connectionPool;

    public ViewCommittee(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        Long committeeId = submission.getLongRequestParameter("committee_id");

        Committee committee = connectionPool.useConnection(connection -> {
            Dao<Committee> committeeDao = DaoBuilders.COMMITTEE.buildDao(connection);
            return committeeDao.selectOne(committeeId);
        });

        HtmlLegupResponse response = HtmlLegupResponse.simpleResponse(this.getClass(), submission.getLoggedInUser());
        response.putVelocityData("committee", committee);
        return response;
    }
}
