package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.ReportCardGrades;
import org.center4racialjustice.legup.util.Tuple;
import org.center4racialjustice.legup.web.Handler;
import org.center4racialjustice.legup.web.LegupSession;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class ViewReportCardLegislators implements Handler {

    @Override
    public VelocityContext handle(Request request, LegupSession legupSession, HttpServletResponse httpServletResponse) {

        String oneTimeKey = request.getParameter("one_time_key");

        ReportCardGrades reportCardGrades = (ReportCardGrades) legupSession.getObject(LegupSession.ReportCardGradesKey, oneTimeKey);

        VelocityContext velocityContext = new VelocityContext();

        Tuple<List<Legislator>, List<Legislator>> tuple = Legislator.splitByChamber(reportCardGrades.getLegislators());

        velocityContext.put("houseMembers", tuple.getFirst());
        velocityContext.put("senateMembers", tuple.getSecond());
        velocityContext.put("reportCardName", reportCardGrades.getReportCardName());
        velocityContext.put("oneTimeKey", oneTimeKey);

        return velocityContext;
    }
}
