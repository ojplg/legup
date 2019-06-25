package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.LegislatorDao;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.NavLink;
import org.center4racialjustice.legup.web.Responder;

import java.util.Collections;
import java.util.List;

public class ViewLegislatorForm implements Responder {

    private final ConnectionPool connectionPool;

    public ViewLegislatorForm(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        long legislatorId = submission.getLongRequestParameter("legislator_id");

        return connectionPool.useConnection(connection -> {
            LegislatorDao legislatorDao = new LegislatorDao(connection);
            Legislator legislator = legislatorDao.read(legislatorId);
            HtmlLegupResponse response = HtmlLegupResponse.withLinks(this.getClass(), submission.getLoggedInUser(), navLinks(legislator.getSessionNumber()));
            response.putVelocityData("legislator", legislator);
            return response;
        });
    }

    private List<NavLink> navLinks(long sessionNumber){
        return Collections.singletonList(
                new NavLink("Legislators Index", "/legup/view_legislators?session_number=" + sessionNumber)
        );
    }

}
