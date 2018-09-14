package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.domain.ReportCardGrades;
import org.center4racialjustice.legup.web.Handler;
import org.center4racialjustice.legup.web.LegupSession;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;

public class ViewReportCardBills implements Handler {

    @Override
    public VelocityContext handle(Request request, LegupSession legupSession, HttpServletResponse httpServletResponse) {

        String oneTimeKey = request.getParameter("one_time_key");

        ReportCardGrades reportCardGrades = (ReportCardGrades) legupSession.getObject(LegupSession.ReportCardGradesKey, oneTimeKey);

        VelocityContext velocityContext = new VelocityContext();

        velocityContext.put("bills", reportCardGrades.getBills());
        velocityContext.put("reportCardName", reportCardGrades.getReportCardName());
        velocityContext.put("oneTimeKey", oneTimeKey);

        return velocityContext;
    }
}
