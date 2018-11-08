package org.center4racialjustice.legup.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.db.BillDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.LegislatorDao;
import org.center4racialjustice.legup.db.ReportCardDao;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Organization;
import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.domain.VoteSide;
import org.center4racialjustice.legup.util.Tuple;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

public class ReportCardPersistence {

    private static final Logger log = LogManager.getLogger(ReportCardPersistence.class);

    private final ConnectionPool connectionPool;

    public ReportCardPersistence(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public ReportCard saveNewCard(String name, long sessionNumber, Organization organization){
        ReportCard reportCard = new ReportCard();
        reportCard.setName(name);
        reportCard.setSessionNumber(sessionNumber);
        reportCard.setOrganization(organization);

        return connectionPool.runAndCommit(
                connection -> {
                    ReportCardDao dao = new ReportCardDao(connection);
                    dao.save(reportCard);
                    return reportCard;
                }
        );
    }

    public ReportCard updateReportCard(long id,
                                        Organization organization,
                                        Map<Long, VoteSide> voteSideByBillIdMap,
                                        List<Long> selectedLegislatorIds){
        return connectionPool.runAndCommit(
                connection -> {
                    ReportCardDao reportCardDao = new ReportCardDao(connection);
                    ReportCard reportCard = reportCardDao.read(id);
                    reportCard.setOrganization(organization);
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

    public SortedMap<Bill, String> computeFactorSettings(ReportCard reportCard){
        return connectionPool.useConnection( connection -> {
            BillDao billDao = new BillDao(connection);
            List<Bill> bills = billDao.readBySession(reportCard.getSessionNumber());
            Collections.sort(bills);
            return reportCard.computeFactorSettings(bills);
        });
    }

    public Tuple<SortedMap<Legislator, Boolean>, SortedMap<Legislator, Boolean>> computeSelectedLegislators(ReportCard reportCard) {
        return connectionPool.useConnection(connection -> {
            LegislatorDao legislatorDao = new LegislatorDao(connection);
            List<Legislator> legislators = legislatorDao.readBySession(reportCard.getSessionNumber());

            log.info("Found legislators " + legislators.size());

            Tuple<List<Legislator>, List<Legislator>> splitLegislators = Legislator.splitByChamber(legislators);

            SortedMap<Legislator, Boolean> selectedHouse = reportCard.findSelectedLegislators(splitLegislators.getFirst());
            SortedMap<Legislator, Boolean> selectedSenate = reportCard.findSelectedLegislators(splitLegislators.getSecond());

            log.info("selected house " + selectedHouse.size());
            log.info("selected senate " + selectedSenate.size());

            return new Tuple<>(selectedHouse, selectedSenate);
        });
    }
}
