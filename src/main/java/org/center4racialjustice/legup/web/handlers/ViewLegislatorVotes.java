package org.center4racialjustice.legup.web.handlers;

import org.center4racialjustice.legup.db.BillActionDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.ConnectionWrapper;
import org.center4racialjustice.legup.db.LegislatorDao;
import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.service.BillActionCollator;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;

import java.util.List;

public class ViewLegislatorVotes implements Responder {

    private final ConnectionPool connectionPool;

    public ViewLegislatorVotes(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        long legislatorId = submission.getLongRequestParameter("legislator_id");

        try (ConnectionWrapper connection = connectionPool.getWrappedConnection()) {
            LegislatorDao legislatorDao = new LegislatorDao(connection);

            Legislator legislator = legislatorDao.read(legislatorId);

            BillActionDao billActionDao = new BillActionDao(connection);

            List<BillAction> billActions = billActionDao.readByLegislator(legislator);
            BillActionCollator collator = new BillActionCollator(billActions);

            LegupResponse response = new LegupResponse(this.getClass());
            response.putVelocityData("votes", collator.getVotes());
            response.putVelocityData("sponsorships", collator.getSponsorships());
            response.putVelocityData("chiefSponsorships", collator.getChiefSponsorships());
            response.putVelocityData("legislator", legislator);

            return response;
        }

    }
}
