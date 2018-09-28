package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.domain.ReportCardGrades;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSession;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;

public class ViewReportCardScoresCsv implements Responder {

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        ReportCardGrades reportCardGrades = (ReportCardGrades) submission.getObject(LegupSession.ReportCardGradesKey);

        LegupResponse legupResponse = LegupResponse.forPlaintext(this.getClass());
        legupResponse.putVelocityData("computer", ReportCard.ScoreComputer);
        legupResponse.putVelocityData("reportCardGrades", reportCardGrades);
        legupResponse.putVelocityData("legislators", reportCardGrades.getLegislators());
        legupResponse.putVelocityData("bills", reportCardGrades.getBills());
        legupResponse.putVelocityData("scores", reportCardGrades.getLookupTable());
        legupResponse.putVelocityData("grades", reportCardGrades.getGrades());
        return legupResponse;
    }
}