package org.center4racialjustice.legup.service;

import org.center4racialjustice.legup.db.BillDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.LegislatorDao;
import org.center4racialjustice.legup.db.ReportCardDao;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.domain.VoteSide;

import java.util.List;
import java.util.Map;

public class ReportCardPersistence {

    private final ConnectionPool connectionPool;

    public ReportCardPersistence(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public ReportCard saveNewCard(String name, long sessionNumber){
        ReportCard reportCard = new ReportCard();
        reportCard.setName(name);
        reportCard.setSessionNumber(sessionNumber);

        return connectionPool.runAndCommit(
                connection -> {
                    ReportCardDao dao = new ReportCardDao(connection);
                    dao.save(reportCard);
                    return reportCard;
                }
        );
    }

    public ReportCard updateReportCard(long id,
                                        Map<Long, VoteSide> voteSideByBillIdMap,
                                        List<Long> selectedLegislatorIds){
        return connectionPool.runAndCommit(
                connection -> {
                    ReportCardDao reportCardDao = new ReportCardDao(connection);
                    ReportCard reportCard = reportCardDao.read(id);
                    Long sessionNumber = reportCard.getSessionNumber();

                    BillDao billDao = new BillDao(connection);
                    List<Bill> bills = billDao.readBySession(sessionNumber);
                    reportCard.resetReportFactorSettings(bills, voteSideByBillIdMap);

                    LegislatorDao legislatorDao = new LegislatorDao(connection);
                    List<Legislator> legislators = legislatorDao.readBySession(sessionNumber);
                    reportCard.resetSelectedLegislators(legislators, selectedLegislatorIds);

                    reportCardDao.save(reportCard);

                    return reportCard;
                }
        );
    }
}
