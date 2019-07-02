package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.Organization;
import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.service.ReportCardPersistence;
import org.center4racialjustice.legup.service.UserService;
import org.center4racialjustice.legup.web.ContinueLegupResponse;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaveNewReportCard implements Responder {

    private final ReportCardPersistence reportCardPersistence;
    private final UserService userService;

    public SaveNewReportCard(ConnectionPool connectionPool){
        this.reportCardPersistence = new ReportCardPersistence(connectionPool);
        this.userService = new UserService(connectionPool);
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        Map<String, String> errors = validate(submission);
        if (errors.size() > 0) {
            return HtmlLegupResponse.forError(
                    ViewNewReportCardForm.class,
                    submission.getLoggedInUser(),
                    "Could not save report card. See error(s) below.",
                    errors);
        }

        String name = submission.getParameter("name");
        long session = submission.getLongRequestParameter("session");
        long organizationId = submission.getLongRequestParameter("organization");

        Organization organization = userService.findUserOrganization(submission.getLoggedInUser(), organizationId);

        ReportCard card = reportCardPersistence.saveNewCard(name, session, organization);

        Long reportCardId = card.getId();

        organization.addReportCard(card);

        ContinueLegupResponse response = new ContinueLegupResponse(ViewReportCardForm.class);
        response.setParameter("report_card_id", String.valueOf(reportCardId));
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
