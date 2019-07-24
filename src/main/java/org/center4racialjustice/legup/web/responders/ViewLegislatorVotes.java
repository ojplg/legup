package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.db.BillActionDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.LegislatorDao;
import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.service.BillActionCollator;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.NavLink;
import org.center4racialjustice.legup.web.Responder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ViewLegislatorVotes implements Responder {

    private final ConnectionPool connectionPool;

    public ViewLegislatorVotes(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        long legislatorId = submission.getLongRequestParameter("legislator_id");

        return connectionPool.useConnection(connection -> {
            LegislatorDao legislatorDao = new LegislatorDao(connection);

            Legislator legislator = legislatorDao.read(legislatorId);

            BillActionDao billActionDao = new BillActionDao(connection);

            List<BillAction> billActions = billActionDao.readByLegislator(legislator);
            BillActionCollator collator = new BillActionCollator(billActions, legislator);

            HtmlLegupResponse response = HtmlLegupResponse.withLinks(this.getClass(), submission.getLoggedInUser(), navLinks(legislator.getSessionNumber()));
            // FIXME: Need some votes here
            response.putVelocityData("votes", collator.getVotes());
            response.putVelocityData("sponsorships", collator.getSponsorships());
            response.putVelocityData("chiefSponsorships", collator.getChiefSponsorships());
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
