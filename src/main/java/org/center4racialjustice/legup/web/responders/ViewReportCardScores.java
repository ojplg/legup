package org.center4racialjustice.legup.web.responders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Grade;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Organization;
import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.domain.ReportCardGrades;
import org.center4racialjustice.legup.service.GradingService;
import org.center4racialjustice.legup.util.LookupTable;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSession;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.NavLink;
import org.center4racialjustice.legup.web.SecuredResponder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ViewReportCardScores implements SecuredResponder {

    private static final Logger log = LogManager.getLogger(ViewReportCardScores.class);

    private final GradingService gradingService;

    public ViewReportCardScores(ConnectionPool connectionPool){
        this.gradingService = new GradingService(connectionPool);
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {

        long reportCardId = submission.getLongRequestParameter( "report_card_id");

        ReportCardGrades reportCardGrades = gradingService.calculate(reportCardId);

        String oneTimeKey = submission.setObject(LegupSession.ReportCardGradesKey, reportCardGrades);

        LookupTable<Legislator, Bill, Integer> scores = reportCardGrades.getLookupTable();
        Map<Legislator, Grade> grades = reportCardGrades.getGrades();

        List<Organization> organizations = submission.getLoggedInUsersOrganizations();
        Organization organization = Organization.findReportCardOwner(organizations, reportCardId);

        List<NavLink> links = navLinks(oneTimeKey, reportCardId, organization.getId());
        HtmlLegupResponse response = HtmlLegupResponse.withHelpAndLinks(this.getClass(), submission.getLoggedInUser(), links);

        response.putVelocityData("oneTimeKey", oneTimeKey);
        response.putVelocityData("scores", scores);
        response.putVelocityData("grades", grades);
        response.putVelocityData("computer", ReportCard.ScoreComputer);
        response.putVelocityData("legislators", reportCardGrades.getLegislators());
        response.putVelocityData("bills", reportCardGrades.getBills());
        response.putVelocityData("reportCard", reportCardGrades.getReportCard());
        response.putVelocityData("reportCardGrades", reportCardGrades);

        return response;
    }

    @Override
    public boolean permitted(LegupSubmission submission){
        long reportCardId = submission.getLongRequestParameter( "report_card_id");
        List<Organization> organizations = submission.getLoggedInUsersOrganizations();
        return Organization.anyOwnCard(organizations, reportCardId);
    }


    private List<NavLink> navLinks(String oneTimeKey, long reportCardId, long organizationId) {
        return Arrays.asList(
                new NavLink("Bill Analysis","/legup/view_report_card_bills?one_time_key=" + oneTimeKey),
                new NavLink("Edit", "/legup/view_report_card_form?report_card_id=" + reportCardId + "&organization_id=" + organizationId),
                new NavLink("CSV", "/legup/view_report_card_scores_csv?one_time_key=" + oneTimeKey)

        );
    }

}
