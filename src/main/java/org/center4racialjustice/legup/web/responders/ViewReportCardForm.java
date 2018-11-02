package org.center4racialjustice.legup.web.responders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.ReportCardDao;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.service.ReportCardPersistence;
import org.center4racialjustice.legup.util.Tuple;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;

import java.util.SortedMap;

public class ViewReportCardForm implements Responder {

    private static final Logger log = LogManager.getLogger(ViewReportCardForm.class);

    private final ConnectionPool connectionPool;
    private final ReportCardPersistence reportCardPersistence;

    public ViewReportCardForm(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
        this.reportCardPersistence = new ReportCardPersistence(connectionPool);
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {

        Long reportCardId = submission.getLongRequestParameter("report_card_id");
        if ( reportCardId == null ){
            return new HtmlLegupResponse(this.getClass());
        }

        log.info("Request for form for " + reportCardId);

        ReportCard reportCard = connectionPool.useConnection(
                connection -> {
                    ReportCardDao reportCardDao = new ReportCardDao(connection);
                    return reportCardDao.read(reportCardId);
                });

        SortedMap<Bill, String> factorSettings = reportCardPersistence.computeFactorSettings(reportCard);
        Tuple<SortedMap<Legislator, Boolean>, SortedMap<Legislator, Boolean>> selectedLegislators =
                reportCardPersistence.computeSelectedLegislators(reportCard);

        HtmlLegupResponse response = new HtmlLegupResponse(this.getClass());

        response.putVelocityData("report_card", reportCard);
        response.putVelocityData("factor_settings", factorSettings);
        response.putVelocityData("selectedHouse", selectedLegislators.getFirst());
        response.putVelocityData("selectedSenate", selectedLegislators.getSecond());

        return response;
    }

}
