package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.db.BillDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.BillActionDao;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.Vote;
import org.center4racialjustice.legup.web.Handler;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class ViewBillVotes implements Handler {

    private final ConnectionPool connectionPool;

    public ViewBillVotes(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public VelocityContext handle(Request request, HttpServletResponse httpServletResponse) throws SQLException {

        String billIdParameter = request.getParameter("bill_id");
        long billId = Long.parseLong(billIdParameter);

        try (Connection connection = connectionPool.getConnection()){
            BillDao billDao = connectionPool.getBillDao();

            Bill bill = billDao.read(billId);

            BillActionDao billActionDao = new BillActionDao(connection);

            List<BillAction> billActions =  billActionDao.readByBill(bill);
            List<Vote> votes = BillAction.filterAndConvertToVotes(billActions);
            Collections.sort(votes);

            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("votes", votes);
            velocityContext.put("bill", bill);

            return velocityContext;
        }

    }
}
