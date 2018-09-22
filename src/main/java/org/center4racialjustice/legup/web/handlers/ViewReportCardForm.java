package org.center4racialjustice.legup.web.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.db.BillDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.ConnectionWrapper;
import org.center4racialjustice.legup.db.LegislatorDao;
import org.center4racialjustice.legup.db.ReportCardDao;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.domain.ReportCardLegislator;
import org.center4racialjustice.legup.domain.ReportFactor;
import org.center4racialjustice.legup.util.Lists;
import org.center4racialjustice.legup.util.Tuple;
import org.center4racialjustice.legup.web.Handler;
import org.center4racialjustice.legup.web.LegupSession;
import org.center4racialjustice.legup.web.Util;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class ViewReportCardForm implements Handler {

    private static final Logger log = LogManager.getLogger(ViewReportCardForm.class);

    private final ConnectionPool connectionPool;

    public ViewReportCardForm(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    @Override
    public VelocityContext handle(Request request, LegupSession legupSession, HttpServletResponse httpServletResponse) {

        Long reportCardId = Util.getLongParameter(request, "report_card_id");
        if ( reportCardId == null ){
            return new VelocityContext();
        }

        log.info("Request for form for " + reportCardId);

        try (ConnectionWrapper connection = connectionPool.getWrappedConnection()){

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

            VelocityContext velocityContext = new VelocityContext();

            velocityContext.put("report_card", reportCard);
            velocityContext.put("factor_settings", factorSettings);
            velocityContext.put("selectedHouse", selectedHouse);
            velocityContext.put("selectedSenate", selectedSenate);

            return velocityContext;
        }
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
