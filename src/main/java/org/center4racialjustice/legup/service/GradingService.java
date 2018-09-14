package org.center4racialjustice.legup.service;

import org.center4racialjustice.legup.db.BillActionDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.ConnectionWrapper;
import org.center4racialjustice.legup.db.LegislatorDao;
import org.center4racialjustice.legup.db.ReportCardDao;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.domain.ReportCardGrades;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GradingService {

    private final ConnectionPool connectionPool;

    public GradingService(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public ReportCardGrades calculate(long reportCardId) {
        try (ConnectionWrapper connection = connectionPool.getWrappedConnection()) {

            ReportCardDao reportCardDao = new ReportCardDao(connection);
            LegislatorDao legislatorDao = new LegislatorDao(connection);
            BillActionDao billActionDao = new BillActionDao(connection);

            ReportCard reportCard = reportCardDao.read(reportCardId);

            long session = reportCard.getSessionNumber();

            List<Legislator> legislators = legislatorDao.readBySession(session);

            List<Bill> bills = reportCard.getReportFactors().stream()
                    .map(rf -> rf.getBill()).collect(Collectors.toList());

            Map<Bill, List<BillAction>> votesByBill = new HashMap<>();
            for (Bill bill : bills) {
                List<BillAction> billActions = billActionDao.readByBill(bill);
                votesByBill.put(bill, billActions);
            }

            return new ReportCardGrades(reportCard, legislators, votesByBill );
        }
    }

}
