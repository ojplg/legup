package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.db.BillDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.ConnectionWrapper;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.util.Tuple;
import org.center4racialjustice.legup.web.Handler;
import org.center4racialjustice.legup.web.LegupSession;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class ViewBills implements Handler {

    private final ConnectionPool connectionPool;

    public ViewBills(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public VelocityContext handle(Request request, LegupSession legupSession, HttpServletResponse httpServletResponse) {

        try (ConnectionWrapper connection = connectionPool.getWrappedConnection() ){

            BillDao dao = new BillDao(connection);
            List<Bill> bills = dao.readAll();

            Tuple<List<Bill>, List<Bill>> dividedBills = Bill.divideAndOrder(bills);

            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("house", Chamber.House);
            velocityContext.put("senate", Chamber.Senate);
            velocityContext.put("house_bills", dividedBills.getFirst());
            velocityContext.put("senate_bills", dividedBills.getSecond());
            return velocityContext;
        }
    }
}
