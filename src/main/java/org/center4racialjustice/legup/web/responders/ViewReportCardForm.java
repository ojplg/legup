package org.center4racialjustice.legup.web.responders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.db.BillDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.LegislatorDao;
import org.center4racialjustice.legup.db.ReportCardDao;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.domain.ReportFactor;
import org.center4racialjustice.legup.util.Lists;
import org.center4racialjustice.legup.util.Tuple;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class ViewReportCardForm implements Responder {

    private static final Logger log = LogManager.getLogger(ViewReportCardForm.class);

    private final ConnectionPool connectionPool;

    public ViewReportCardForm(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {

        Long reportCardId = submission.getLongRequestParameter("report_card_id");
        if ( reportCardId == null ){
            return new LegupResponse(this.getClass());
        }

        log.info("Request for form for " + reportCardId);

        return connectionPool.useConnection( connection -> {

            ReportCardDao reportCardDao = new ReportCardDao(connection);
            ReportCard reportCard = reportCardDao.read(reportCardId);

            Long sessionId = reportCard.getSessionNumber();

            BillDao billDao = new BillDao(connection);
            List<Bill> bills = billDao.readBySession(sessionId);
            Collections.sort(bills);
            SortedMap<Bill, String> factorSettings = computeFactorSettings(bills, reportCard);

            LegislatorDao legislatorDao = new LegislatorDao(connection);
            List<Legislator> legislators = legislatorDao.readBySession(sessionId);

            log.info("Found legislators " + legislators.size());

            Tuple<List<Legislator>, List<Legislator>> splitLegislators = Legislator.splitByChamber(legislators);

            SortedMap<Legislator, Boolean> selectedHouse = reportCard.findSelectedLegislators(splitLegislators.getFirst());
            SortedMap<Legislator, Boolean> selectedSenate = reportCard.findSelectedLegislators(splitLegislators.getSecond());

            log.info("selected house " + selectedHouse.size());
            log.info("selected senate " + selectedSenate.size());

            LegupResponse response = new LegupResponse(this.getClass());

            response.putVelocityData("report_card", reportCard);
            response.putVelocityData("factor_settings", factorSettings);
            response.putVelocityData("selectedHouse", selectedHouse);
            response.putVelocityData("selectedSenate", selectedSenate);

            return response;
        });
    }

    private SortedMap<Bill, String> computeFactorSettings(List<Bill> bills, ReportCard reportCard){
        List<ReportFactor> factors = reportCard.getReportFactors();
        Map<Long, ReportFactor> factorsByBillId = Lists.asMap(factors, f -> f.getBill().getId());

        SortedMap<Bill, String> factorSettings = new TreeMap<>();

        for(Bill bill : bills){
            ReportFactor matchingFactor = factorsByBillId.get(bill.getId());
            if ( matchingFactor == null ){
                factorSettings.put(bill, "Unselected");
            } else {
                factorSettings.put(bill, matchingFactor.getVoteSide().getCode());
            }

        }
        return factorSettings;
    }

}
