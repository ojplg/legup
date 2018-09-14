package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.ReportCardBillAnalysis;
import org.center4racialjustice.legup.domain.ReportCardGrades;
import org.center4racialjustice.legup.domain.VoteSide;
import org.center4racialjustice.legup.web.Handler;
import org.center4racialjustice.legup.web.LegupSession;
import org.center4racialjustice.legup.web.Util;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;

public class ViewReportCardBill implements Handler {

    @Override
    public VelocityContext handle(Request request, LegupSession legupSession, HttpServletResponse httpServletResponse) {

        String oneTimeKey = request.getParameter("one_time_key");
        Long billId = Util.getLongParameter(request,"bill_id");

        ReportCardGrades reportCardGrades = (ReportCardGrades) legupSession.getObject(LegupSession.ReportCardGradesKey, oneTimeKey);
        ReportCardBillAnalysis reportCardBillAnalysis = reportCardGrades.getBillAnalysis(billId);

        VelocityContext velocityContext = new VelocityContext();

        velocityContext.put("oneTimeKey", oneTimeKey);
        velocityContext.put("reportCardGrades", reportCardGrades);
        velocityContext.put("reportCardBillAnalysis", reportCardBillAnalysis);

        velocityContext.put("house", Chamber.House);
        velocityContext.put("senate", Chamber.Senate);

        velocityContext.put("yea", VoteSide.Yea);
        velocityContext.put("nay", VoteSide.Nay);
        velocityContext.put("notVoting", VoteSide.NotVoting);
        velocityContext.put("present", VoteSide.Present);

        velocityContext.put("sides", VoteSide.AllSides);

        return velocityContext;
    }
}
