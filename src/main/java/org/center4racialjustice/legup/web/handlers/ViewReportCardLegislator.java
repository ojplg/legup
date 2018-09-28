package org.center4racialjustice.legup.web.handlers;

import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.ReportCardGrades;
import org.center4racialjustice.legup.domain.ReportCardLegislatorAnalysis;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSession;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;

import java.util.Comparator;

public class ViewReportCardLegislator implements Responder {

    @Override
    public LegupResponse handle(LegupSubmission submission) {

        String oneTimeKey = submission.getParameter("one_time_key");
        Long legislatorId = submission.getLongRequestParameter("legislator_id");

        ReportCardGrades reportCardGrades = (ReportCardGrades) submission.getObject(LegupSession.ReportCardGradesKey);
        ReportCardLegislatorAnalysis reportCardLegislatorAnalysis = reportCardGrades.getLegislatorAnalysis(legislatorId);

        LegupResponse response = new LegupResponse(this.getClass());

        response.putVelocityData("oneTimeKey", oneTimeKey);
        response.putVelocityData("reportCardGrades", reportCardGrades);
        response.putVelocityData("reportCardLegislatorAnalysis", reportCardLegislatorAnalysis);

        response.putVelocityData("billComparator", Comparator.naturalOrder());

        response.putVelocityData("voteKey", BillActionType.VOTE);
        response.putVelocityData("sponsorKey", BillActionType.SPONSOR);
        response.putVelocityData("chiefSponsorKey", BillActionType.CHIEF_SPONSOR);

        return response;
    }
}
