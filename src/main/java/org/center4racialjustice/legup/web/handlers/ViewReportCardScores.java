package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Grade;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.domain.ReportCardGrades;
import org.center4racialjustice.legup.service.GradingService;
import org.center4racialjustice.legup.util.LookupTable;
import org.center4racialjustice.legup.web.Handler;
import org.center4racialjustice.legup.web.LegupSession;
import org.center4racialjustice.legup.web.Util;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class ViewReportCardScores implements Handler {

    private final GradingService gradingService;

    public ViewReportCardScores(ConnectionPool connectionPool){
        this.gradingService = new GradingService(connectionPool);
    }

    @Override
    public VelocityContext handle(Request request, LegupSession legupSession, HttpServletResponse httpServletResponse) {

        long reportCardId = Util.getLongParameter(request, "report_card_id");

        ReportCardGrades reportCardGrades = gradingService.calculate(reportCardId);

        String oneTimeKey = legupSession.setObject(LegupSession.ReportCardGradesKey, reportCardGrades);

        LookupTable<Legislator, Bill, Integer> scores = reportCardGrades.getLookupTable();
        Map<Legislator, Grade> grades = reportCardGrades.getGrades();

        VelocityContext velocityContext = new VelocityContext();

        velocityContext.put("oneTimeKey", oneTimeKey);
        velocityContext.put("scores", scores);
        velocityContext.put("grades", grades);
        velocityContext.put("computer", ReportCard.ScoreComputer);
        velocityContext.put("legislators", reportCardGrades.getLegislators());
        velocityContext.put("bills", reportCardGrades.getBills());
        velocityContext.put("reportCard", reportCardGrades.getReportCard());

        return velocityContext;
    }
}
