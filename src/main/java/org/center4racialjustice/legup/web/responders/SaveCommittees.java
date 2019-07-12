package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.DaoBuilders;
import org.center4racialjustice.legup.domain.Committee;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSession;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;
import org.hrorm.Dao;

import java.util.List;

public class SaveCommittees implements Responder {

    private final ConnectionPool connectionPool;

    public SaveCommittees(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        List<Committee> committeeList = (List<Committee>) submission.getObject(LegupSession.CommitteeDataKey);

        int count = connectionPool.runAndCommit(connection -> {
            int savedCount = 0;
            Dao<Committee> committeeDao = DaoBuilders.COMMITTEE.buildDao(connection);
            for(Committee committee : committeeList){
                committeeDao.insert(committee);
                savedCount++;
            }
            return savedCount;
        });

        HtmlLegupResponse response = HtmlLegupResponse.simpleResponse(this.getClass(), submission.getLoggedInUser());
        response.putVelocityData("submitted_committee_count", committeeList.size());
        response.putVelocityData("saved_committee_count", count);

        return response;
    }

}