package org.center4racialjustice.legup.service;

import org.center4racialjustice.legup.db.BillActionDao;
import org.center4racialjustice.legup.db.BillDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.LegislatorDao;
import org.center4racialjustice.legup.db.ReportCardDao;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.GradeCalculator;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.util.LookupTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradingService {

    private final ConnectionPool connectionPool;

    public GradingService(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public LookupTable<Legislator, Bill, Integer> calculate(long reportCardId) throws SQLException {
        try (Connection connection = connectionPool.getConnection()) {

            ReportCardDao reportCardDao = new ReportCardDao(connection);
            LegislatorDao legislatorDao = new LegislatorDao(connection);
            BillDao billDao = connectionPool.getBillDao();
            BillActionDao billActionDao = new BillActionDao(connection);

            ReportCard reportCard = reportCardDao.read(reportCardId);

            long session = reportCard.getSessionNumber();

            List<Legislator> legislators = legislatorDao.readBySession(session);

            GradeCalculator calculator = new GradeCalculator(reportCard, legislators);
            List<Long> billIds = calculator.extractBillIds();

            List<Bill> bills = billDao.readByIds(billIds);
            Map<Bill, List<BillAction>> votesByBill = new HashMap<>();
            for (Bill bill : bills) {
                List<BillAction> billActions = billActionDao.readByBill(bill);
                votesByBill.put(bill, billActions);
            }

            LookupTable<Legislator, Bill, Integer> scores = calculator.calculate(votesByBill);

            return scores;
        }

    }
}
