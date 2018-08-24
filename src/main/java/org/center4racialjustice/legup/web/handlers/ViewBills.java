package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.db.BillDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.util.Tuple;
import org.center4racialjustice.legup.web.Handler;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ViewBills implements Handler {

    private final ConnectionPool connectionPool;

    public ViewBills(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public VelocityContext handle(Request request, HttpServletResponse httpServletResponse) throws SQLException {

        try (Connection connection = connectionPool.getConnection() ){
            BillDao dao = new BillDao(connection);

            List<Bill> bills = dao.readAll();
            Tuple<List<Bill>, List<Bill>> divideBills = Bill.divideAndOrder(bills);

            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("house_bills", divideBills.getFirst());
            velocityContext.put("senate_bills", divideBills.getSecond());
            return velocityContext;
        }
    }
}
