package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.ReportCardBillAnalysis;
import org.center4racialjustice.legup.domain.ReportCardGrades;
import org.center4racialjustice.legup.domain.VoteSide;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSession;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.NavLink;
import org.center4racialjustice.legup.web.Responder;

import java.util.Arrays;
import java.util.List;

public class ViewReportCardBill implements Responder {

    @Override
    public LegupResponse handle(LegupSubmission submission) {

        String oneTimeKey = submission.getParameter("one_time_key");
        Long billId = submission.getLongRequestParameter("bill_id");

        ReportCardGrades reportCardGrades = (ReportCardGrades) submission.getObject(LegupSession.ReportCardGradesKey);
        ReportCardBillAnalysis reportCardBillAnalysis = reportCardGrades.getBillAnalysis(billId);

        HtmlLegupResponse response = HtmlLegupResponse.withLinks(this.getClass(),
                submission.getLoggedInUser(), navLinks(reportCardGrades.getReportCard().getId()));

        response.putVelocityData("oneTimeKey", oneTimeKey);
        response.putVelocityData("reportCardGrades", reportCardGrades);
        response.putVelocityData("reportCardBillAnalysis", reportCardBillAnalysis);

        response.putVelocityData("house", Chamber.House);
        response.putVelocityData("senate", Chamber.Senate);

        response.putVelocityData("yea", VoteSide.Yea);
        response.putVelocityData("nay", VoteSide.Nay);
        response.putVelocityData("notVoting", VoteSide.NotVoting);
        response.putVelocityData("present", VoteSide.Present);

        response.putVelocityData("sides", VoteSide.AllSides);

        return response;
    }

    private List<NavLink> navLinks(long reportCardId){
        return Arrays.asList(
                new NavLink("All Scores", "/legup/view_report_card_scores?report_card_id=" + reportCardId)
        );
    }

}
