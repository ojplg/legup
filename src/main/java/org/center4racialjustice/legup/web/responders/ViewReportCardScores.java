package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Grade;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.domain.ReportCardGrades;
import org.center4racialjustice.legup.service.GradingService;
import org.center4racialjustice.legup.util.LookupTable;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSession;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;

import java.util.Map;

public class ViewReportCardScores implements Responder {

    private final GradingService gradingService;

    public ViewReportCardScores(ConnectionPool connectionPool){
        this.gradingService = new GradingService(connectionPool);
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {

        long reportCardId = submission.getLongRequestParameter( "report_card_id");

        ReportCardGrades reportCardGrades = gradingService.calculate(reportCardId);

        String oneTimeKey = submission.setObject(LegupSession.ReportCardGradesKey, reportCardGrades);

        LookupTable<Legislator, Bill, Integer> scores = reportCardGrades.getLookupTable();
        Map<Legislator, Grade> grades = reportCardGrades.getGrades();

        HtmlLegupResponse response = new HtmlLegupResponse(this.getClass());

        response.putVelocityData("oneTimeKey", oneTimeKey);
        response.putVelocityData("scores", scores);
        response.putVelocityData("grades", grades);
        response.putVelocityData("computer", ReportCard.ScoreComputer);
        response.putVelocityData("legislators", reportCardGrades.getLegislators());
        response.putVelocityData("bills", reportCardGrades.getBills());
        response.putVelocityData("reportCard", reportCardGrades.getReportCard());
        response.putVelocityData("reportCardGrades", reportCardGrades);

        return response;
    }
}
