package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.service.ReportCardPersistence;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;

import java.util.HashMap;
import java.util.Map;

public class SaveNewReportCard implements Responder {

    private final ReportCardPersistence reportCardPersistence;

    public SaveNewReportCard(ConnectionPool connectionPool){
        this.reportCardPersistence = new ReportCardPersistence(connectionPool);
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        Map<String, String> errors = validate(submission);
        if (errors.size() > 0) {
            return LegupResponse.forError(
                    ViewReportCardForm.class, "Could not save report card. See error(s) below.", errors);
        }

        String name = submission.getParameter("name");
        long session = submission.getLongRequestParameter("session");

        ReportCard card = reportCardPersistence.saveNewCard(name, session);

        LegupResponse response = new LegupResponse(ViewReportCardForm.class);
        response.putVelocityData("report_card", card);
        return response;

    }

    private Map<String, String> validate(LegupSubmission legupSubmission){
        Map<String, String> errors = new HashMap<>();
        if( ! legupSubmission.isValidLongParameter("session")){
            errors.put("session","Could not parse session number");
        }
        if ( ! legupSubmission.isNonEmptyStringParameter("name")){
            errors.put("name","Name was empty");
        }
        return errors;
    }


}
