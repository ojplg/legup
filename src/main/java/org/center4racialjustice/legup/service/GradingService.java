package org.center4racialjustice.legup.service;

import org.center4racialjustice.legup.db.BillActionDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.ConnectionWrapper;
import org.center4racialjustice.legup.db.ReportCardDao;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.domain.ReportCardGrades;
import org.center4racialjustice.legup.domain.ReportFactor;

import java.util.ArrayList;
import java.util.List;

public class GradingService {

    private final ConnectionPool connectionPool;

    public GradingService(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public ReportCardGrades calculate(long reportCardId) {
        try (ConnectionWrapper connection = connectionPool.getWrappedConnection()) {

            ReportCardDao reportCardDao = new ReportCardDao(connection);
            BillActionDao billActionDao = new BillActionDao(connection);

            ReportCard reportCard = reportCardDao.read(reportCardId);

            List<BillAction> actions = new ArrayList<>();
            for (ReportFactor factor : reportCard.getReportFactors()) {
                Bill bill = factor.getBill();
                List<BillAction> billActions = billActionDao.readByBill(bill);
                actions.addAll(billActions);
            }

            return new ReportCardGrades(reportCard, actions);
        }
    }

}
