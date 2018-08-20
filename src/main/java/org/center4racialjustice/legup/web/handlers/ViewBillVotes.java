package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.db.BillDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.VoteDao;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Vote;
import org.center4racialjustice.legup.web.Handler;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ViewBillVotes implements Handler {

    private final ConnectionPool connectionPool;

    public ViewBillVotes(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public VelocityContext handle(Request request, HttpServletResponse httpServletResponse) throws IOException, SQLException {

        String billIdParameter = request.getParameter("bill_id");
        long billId = Long.parseLong(billIdParameter);

        Connection connection = null;

        try {
            connection = connectionPool.getConnection();
            BillDao billDao = new BillDao(connection);

            Bill bill = billDao.read(billId);

            VoteDao voteDao = new VoteDao(connection);

            List<Vote> votes = voteDao.readByBill(bill);

            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("votes", votes);
            velocityContext.put("bill", bill);

            return velocityContext;
        } finally {
            if (connection !=null){
                connection.close();
            }
        }

    }
}
