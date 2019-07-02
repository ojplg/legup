package org.center4racialjustice.legup.web.responders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.GradeLevel;
import org.center4racialjustice.legup.domain.GradeLevels;
import org.center4racialjustice.legup.domain.Organization;
import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.domain.User;
import org.center4racialjustice.legup.domain.VoteSide;
import org.center4racialjustice.legup.service.ReportCardPersistence;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.NavLink;
import org.center4racialjustice.legup.web.Responder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaveReportCard implements Responder {

    private static final Logger log = LogManager.getLogger(SaveReportCard.class);

    private final ReportCardPersistence reportCardPersistence;

    public SaveReportCard(ConnectionPool connectionPool){
        this.reportCardPersistence = new ReportCardPersistence(connectionPool);
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {

        User user = submission.getLoggedInUser();
        Long reportCardId = submission.getLongRequestParameter( "report_card_id");
        Long organizationId = submission.getLongRequestParameter( "organization_id");

        Organization organization = submission.getLoggedInUserOrganization(organizationId);
        List<Long> legislatorIds = parseCheckedLegislators(submission);
        Map<Long, VoteSide> voteSideMap = parseVoteSidesByBillIdMap(submission);
        List<GradeLevel> gradeLevels = parseGradeLevels(submission);
        ReportCard card = reportCardPersistence.updateReportCard(reportCardId, organization, voteSideMap, legislatorIds, gradeLevels);

        HtmlLegupResponse response = HtmlLegupResponse.withLinks(this.getClass(), user, navLinks(reportCardId, organizationId));
        response.putVelocityData("reportCard", card);
        return response;
    }

    private List<Long> parseCheckedLegislators(LegupSubmission submission){
        Map<Long, String> legislatorParameters = submission.extractNumberedParameters("legislator_");
        return new ArrayList<>(legislatorParameters.keySet());
    }

    private Map<Long, VoteSide> parseVoteSidesByBillIdMap(LegupSubmission submission){
        Map<Long, String> voteSideParameters = submission.extractNumberedParameters("bill_vote_side_");
        Map<Long, VoteSide> voteSidesByBillIdMap = new HashMap<>();
        for( Map.Entry<Long, String> entry : voteSideParameters.entrySet() ){
            String voteString = entry.getValue();
            if ( voteString.equalsIgnoreCase("Yes") || voteString.equalsIgnoreCase("No")) {
                VoteSide voteSide = VoteSide.fromCode(voteString.substring(0, 1));
                voteSidesByBillIdMap.put(entry.getKey(), voteSide);
            }
        }
       return voteSidesByBillIdMap;
    }

    private List<GradeLevel> parseGradeLevels(LegupSubmission submission){
        List<GradeLevel> gradeLevels = new ArrayList<>();
        for( String grade : GradeLevels.REQUIRED_GRADES){
            long housePctg = submission.getLongRequestParameter("house_grade_" + grade.toLowerCase());
            GradeLevel houseLevel = new GradeLevel(Chamber.House, grade, housePctg);
            gradeLevels.add(houseLevel);
            long senatePctg = submission.getLongRequestParameter("senate_grade_" + grade.toLowerCase());
            GradeLevel senateLevel = new GradeLevel(Chamber.Senate, grade, senatePctg);
            gradeLevels.add(senateLevel);
        }
        return gradeLevels;
    }


    @Override
    public boolean isSecured() {
        return true;
    }

    @Override
    public boolean permitted(LegupSubmission legupSubmission) {
        Long reportCardId = legupSubmission.getLongRequestParameter( "report_card_id");
        Long organizationId = legupSubmission.getLongRequestParameter("organization_id");
        Organization organization = legupSubmission.getLoggedInUserOrganization(organizationId);
        boolean answer = organization.ownsCard(reportCardId);
        log.info("User " + legupSubmission.getLoggedInUser() + " in organization " + organization.getName() +
                " attempted to modify " + reportCardId  +
                " permission was determined " + answer);
        return answer;
    }

    private List<NavLink> navLinks(long reportCardId, long organizationId) {
        return Arrays.asList(
                new NavLink("Edit", "/legup/view_report_card_form?report_card_id=" + reportCardId + "&organization_id=" + organizationId),
                new NavLink("Calculate", "/legup/view_report_card_scores?report_card_id=" + reportCardId)

        );
    }
}
