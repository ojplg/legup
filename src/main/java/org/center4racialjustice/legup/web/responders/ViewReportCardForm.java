package org.center4racialjustice.legup.web.responders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Organization;
import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.service.ReportCardPersistence;
import org.center4racialjustice.legup.util.Tuple;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.NavLink;
import org.center4racialjustice.legup.web.SecuredResponder;

import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;

public class ViewReportCardForm implements SecuredResponder {

    private static final Logger log = LogManager.getLogger(ViewReportCardForm.class);

    private final ReportCardPersistence reportCardPersistence;

    public ViewReportCardForm(ConnectionPool connectionPool){
        this.reportCardPersistence = new ReportCardPersistence(connectionPool);
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {

        Long reportCardId = submission.getLongRequestParameter("report_card_id");
        if ( reportCardId == null ){
            return HtmlLegupResponse.simpleResponse(this.getClass(), submission.getLoggedInUser());
        }

        log.info("Request for form for " + reportCardId);

        ReportCard reportCard = reportCardPersistence.selectCard(reportCardId);

        SortedMap<Bill, String> factorSettings = reportCardPersistence.computeFactorSettings(reportCard);
        Tuple<SortedMap<Legislator, Boolean>, SortedMap<Legislator, Boolean>> selectedLegislators =
                reportCardPersistence.computeSelectedLegislators(reportCard);

        HtmlLegupResponse response = HtmlLegupResponse.withHelpAndLinks(this.getClass(),
                submission.getLoggedInUser(), navLinks(reportCardId));

        response.putVelocityData("report_card", reportCard);
        response.putVelocityData("factor_settings", factorSettings);
        response.putVelocityData("selectedHouse", selectedLegislators.getFirst());
        response.putVelocityData("selectedSenate", selectedLegislators.getSecond());

        return response;
    }

    private List<NavLink> navLinks(long reportCardId) {
        return Arrays.asList(
                new NavLink("Report Card Index", "/legup/view_report_cards"),
                new NavLink("Calculate Scores","/legup/view_report_card_scores?report_card_id=" + reportCardId)
        );
    }

    @Override
    public boolean permitted(LegupSubmission legupSubmission) {
        Long reportCardId = legupSubmission.getLongRequestParameter("report_card_id");
        ReportCard reportCard = reportCardPersistence.selectCard(reportCardId);
        Organization organization = legupSubmission.getOrganization();
        return organization.equals(reportCard.getOrganization());
    }
}
