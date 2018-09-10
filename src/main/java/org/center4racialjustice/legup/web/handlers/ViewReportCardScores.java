package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.GradeCalculator;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.service.GradingService;
import org.center4racialjustice.legup.util.LookupTable;
import org.center4racialjustice.legup.web.Handler;
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
    public VelocityContext handle(Request request, HttpServletResponse httpServletResponse) {

        Long reportCardId = Util.getLongParameter(request, "report_card_id");

        LookupTable<Legislator, Bill, Integer> scores = gradingService.calculate(reportCardId);
        Map<Legislator, String> grades = GradeCalculator.assignGrades(scores);

        VelocityContext velocityContext = new VelocityContext();

        velocityContext.put("scores", scores);
        velocityContext.put("grades", grades);
        velocityContext.put("computer", GradeCalculator.ScoreComputer);
        velocityContext.put("legislators", scores.sortedRowHeadings(Legislator::compareTo));
        velocityContext.put("bills", scores.sortedColumnHeadings(Bill::compareTo));

        return velocityContext;
    }
}
