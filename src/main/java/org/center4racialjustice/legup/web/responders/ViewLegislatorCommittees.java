package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.db.BillActionDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.DaoBuilders;
import org.center4racialjustice.legup.db.LegislatorDao;
import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.Committee;
import org.center4racialjustice.legup.domain.CommitteeMember;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.service.BillActionCollator;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.NavLink;
import org.center4racialjustice.legup.web.Responder;
import org.hrorm.Dao;

import java.util.Collections;
import java.util.List;

import static org.hrorm.Operator.EQUALS;
import static org.hrorm.Where.inLong;
import static org.hrorm.Where.where;

public class ViewLegislatorCommittees implements Responder {

    private final ConnectionPool connectionPool;

    public ViewLegislatorCommittees(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        long legislatorId = submission.getLongRequestParameter("legislator_id");

        return connectionPool.useConnection(connection -> {
            LegislatorDao legislatorDao = new LegislatorDao(connection);

            Legislator legislator = legislatorDao.read(legislatorId);

            Dao<CommitteeMember> committeeMemberDao = DaoBuilders.COMMITTEE_MEMBERS.buildDao(connection);

            List<Long> committeeIds = committeeMemberDao.selectDistinct("COMMITTEE_ID",
                    where("legislator_id", EQUALS, legislatorId));

            Dao<Committee> committeeDao = DaoBuilders.COMMITTEE.buildDao(connection);
            List<Committee> committees = committeeDao.select(inLong("ID", committeeIds));

            HtmlLegupResponse response = HtmlLegupResponse.withLinks(this.getClass(), submission.getLoggedInUser(), navLinks(legislator.getSessionNumber()));
            response.putVelocityData("legislator", legislator);
            response.putVelocityData("committees", committees);

            return response;
        });
    }

    private List<NavLink> navLinks(long sessionNumber){
        return Collections.singletonList(
                new NavLink("Legislators Index", "/legup/view_legislators?session_number=" + sessionNumber)
        );
    }

}
