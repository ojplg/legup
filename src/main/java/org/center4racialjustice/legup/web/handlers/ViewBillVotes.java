package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.db.BillDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.BillActionDao;
import org.center4racialjustice.legup.db.ConnectionWrapper;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.BillActionSummary;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.VoteSide;
import org.center4racialjustice.legup.web.Handler;
import org.center4racialjustice.legup.web.LegupSession;
import org.center4racialjustice.legup.web.Util;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class ViewBillVotes implements Handler {

    private final ConnectionPool connectionPool;

    public ViewBillVotes(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public VelocityContext handle(Request request, LegupSession legupSession, HttpServletResponse httpServletResponse) {

        long billId = Util.getLongParameter(request,"bill_id");

        try (ConnectionWrapper connection = connectionPool.getWrappedConnection()){
            BillDao billDao = new BillDao(connection);

            Bill bill = billDao.read(billId);
            BillActionDao billActionDao = new BillActionDao(connection);

            List<BillAction> billActions = billActionDao.readByBill(bill);
            BillActionSummary billActionSummary = new BillActionSummary(billActions);

            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("billActionSummary", billActionSummary);
            velocityContext.put("bill", bill);

            velocityContext.put("house", Chamber.House);
            velocityContext.put("senate", Chamber.Senate);

            velocityContext.put("yea", VoteSide.Yea);
            velocityContext.put("nay", VoteSide.Nay);
            velocityContext.put("notVoting", VoteSide.NotVoting);
            velocityContext.put("present", VoteSide.Present);

            velocityContext.put("sides", VoteSide.AllSides);

            return velocityContext;
        }

    }
}
