package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.db.BillActionDao;
import org.center4racialjustice.legup.db.BillDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Vote;
import org.center4racialjustice.legup.util.Lists;
import org.center4racialjustice.legup.util.Tuple;
import org.center4racialjustice.legup.web.Handler;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ViewBillSponsors implements Handler {

    private final ConnectionPool connectionPool;

    public ViewBillSponsors(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public VelocityContext handle(Request request, HttpServletResponse httpServletResponse) throws SQLException {

        String billIdParameter = request.getParameter("bill_id");
        long billId = Long.parseLong(billIdParameter);

        try (Connection connection = connectionPool.getConnection()){
            BillDao billDao = new BillDao(connection);

            Bill bill = billDao.read(billId);

            BillActionDao billActionDao = new BillActionDao(connection);

            List<BillAction> billActions =  billActionDao.readByBill(bill);
            List<Legislator> sponsors = billActions.stream()
                    .filter(act -> act.getBillActionType().equals(BillActionType.SPONSOR))
                    .map(BillAction::getLegislator)
                    .collect(Collectors.toList());

            Tuple<List<Legislator>, List<Legislator>> sponsorsTuple =
                    Lists.divide(sponsors, leg -> leg.getChamber().equals(Chamber.House));

            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("house_sponsors", sponsorsTuple.getFirst());
            velocityContext.put("senate_sponsors", sponsorsTuple.getSecond());
            velocityContext.put("bill", bill);

            return velocityContext;
        }

    }
}
