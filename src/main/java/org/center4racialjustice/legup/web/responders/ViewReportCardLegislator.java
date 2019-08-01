package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.ReportCardGrades;
import org.center4racialjustice.legup.domain.ReportCardLegislatorAnalysis;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSession;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.NavLink;
import org.center4racialjustice.legup.web.Responder;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ViewReportCardLegislator implements Responder {

    @Override
    public LegupResponse handle(LegupSubmission submission) {

        String oneTimeKey = submission.getParameter("one_time_key");
        Long legislatorId = submission.getLongRequestParameter("legislator_id");

        ReportCardGrades reportCardGrades = (ReportCardGrades) submission.getObject(LegupSession.ReportCardGradesKey);
        ReportCardLegislatorAnalysis reportCardLegislatorAnalysis = reportCardGrades.getLegislatorAnalysis(legislatorId);

        HtmlLegupResponse response = HtmlLegupResponse.withLinks(this.getClass(),
                submission.getLoggedInUser(), navLinks(reportCardGrades.getReportCard().getId()));

        response.putVelocityData("oneTimeKey", oneTimeKey);
        response.putVelocityData("reportCardGrades", reportCardGrades);
        response.putVelocityData("reportCardLegislatorAnalysis", reportCardLegislatorAnalysis);

        response.putVelocityData("billComparator", Comparator.naturalOrder());

        response.putVelocityData("voteKey", BillActionType.VOTE);
        response.putVelocityData("sponsorKey", BillActionType.SPONSOR);
        response.putVelocityData("chiefSponsorKey", BillActionType.CHIEF_SPONSOR);
        response.putVelocityData("introductionKey", BillActionType.INTRODUCE);

        return response;
    }

    private List<NavLink> navLinks(long reportCardId){
        return Arrays.asList(
                new NavLink("All Scores", "/legup/view_report_card_scores?report_card_id=" + reportCardId)
        );
    }

}
