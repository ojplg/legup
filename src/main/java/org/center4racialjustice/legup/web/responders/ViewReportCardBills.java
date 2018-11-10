package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.domain.ReportCardGrades;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSession;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.NavLink;
import org.center4racialjustice.legup.web.Responder;

import java.util.Arrays;
import java.util.List;

public class ViewReportCardBills implements Responder {

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        ReportCardGrades reportCardGrades = (ReportCardGrades) submission.getObject(LegupSession.ReportCardGradesKey);
        String oneTimeKey = submission.getParameter("one_time_key");

        HtmlLegupResponse response = HtmlLegupResponse.withLinks(this.getClass(),
                submission.getLoggedInUser(), navLinks(reportCardGrades.getReportCard().getId()));

        response.putVelocityData("bills", reportCardGrades.getBills());
        response.putVelocityData("reportCardName", reportCardGrades.getReportCardName());
        response.putVelocityData("reportCardId", reportCardGrades.getReportCard().getId());
        response.putVelocityData("oneTimeKey", oneTimeKey);

        return response;
    }


    private List<NavLink> navLinks(long reportCardId){
        return Arrays.asList(
                new NavLink("All Scores", "/legup/view_report_card_scores?report_card_id=" + reportCardId)
        );
    }

}
