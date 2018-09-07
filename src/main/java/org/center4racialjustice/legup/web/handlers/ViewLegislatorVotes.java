package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.ConnectionWrapper;
import org.center4racialjustice.legup.db.LegislatorDao;
import org.center4racialjustice.legup.db.BillActionDao;
import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Vote;
import org.center4racialjustice.legup.web.Handler;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class ViewLegislatorVotes implements Handler {

    private final ConnectionPool connectionPool;

    public ViewLegislatorVotes(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public VelocityContext handle(Request request, HttpServletResponse httpServletResponse) {

        String legislatorIdParameter = request.getParameter("legislator_id");
        long legislatorId = Long.parseLong(legislatorIdParameter);

        try (ConnectionWrapper connection = connectionPool.getWrappedConnection()) {
            LegislatorDao legislatorDao = new LegislatorDao(connection);

            Legislator legislator = legislatorDao.read(legislatorId);

            BillActionDao billActionDao = new BillActionDao(connection);

            List<BillAction> billActions = billActionDao.readByLegislator(legislator);
            List<Vote> votes = BillAction.filterAndConvertToVotes(billActions);

            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("votes", votes);
            velocityContext.put("legislator", legislator);

            return velocityContext;
        }

    }
}
