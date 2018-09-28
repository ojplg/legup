package org.center4racialjustice.legup.web.handlers;

import org.center4racialjustice.legup.db.BillDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.ConnectionWrapper;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.util.Tuple;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;

import java.util.List;

public class ViewBills implements Responder {

    private final ConnectionPool connectionPool;

    public ViewBills(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        try (ConnectionWrapper connection = connectionPool.getWrappedConnection() ){

            BillDao dao = new BillDao(connection);
            List<Bill> bills = dao.readAll();

            Tuple<List<Bill>, List<Bill>> dividedBills = Bill.divideAndOrder(bills);

            LegupResponse response = new LegupResponse(this.getClass());
            response.putVelocityData("house", Chamber.House);
            response.putVelocityData("senate", Chamber.Senate);
            response.putVelocityData("house_bills", dividedBills.getFirst());
            response.putVelocityData("senate_bills", dividedBills.getSecond());
            return response;
        }

    }
}
