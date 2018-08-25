package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.service.GradingService;
import org.center4racialjustice.legup.util.LookupTable;
import org.center4racialjustice.legup.web.Handler;
import org.center4racialjustice.legup.web.Util;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.function.BinaryOperator;

public class ViewReportCardScores implements Handler {

    private final GradingService gradingService;

    public ViewReportCardScores(ConnectionPool connectionPool){
        this.gradingService = new GradingService(connectionPool);
    }

    @Override
    public VelocityContext handle(Request request, HttpServletResponse httpServletResponse) throws SQLException {
        VelocityContext velocityContext = new VelocityContext();

        Long reportCardId = Util.getLongParameter(request, "report_card_id");

        LookupTable<Legislator, Bill, Integer> scores = gradingService.calculate(reportCardId);

        velocityContext.put("scores", scores);
        BinaryOperator<Integer> scoreComputer = (i, j) -> i + j;
        velocityContext.put("computer", scoreComputer);

        return velocityContext;
    }
}
