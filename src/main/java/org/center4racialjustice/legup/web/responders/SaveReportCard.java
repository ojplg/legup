package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.db.ConnectionPool;
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

    private final ReportCardPersistence reportCardPersistence;

    public SaveReportCard(ConnectionPool connectionPool){
        this.reportCardPersistence = new ReportCardPersistence(connectionPool);
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {

        User user = submission.getLoggedInUser();
        Long id = submission.getLongRequestParameter( "id");

        if( id == null ){
            // TODO handle error
        }

        Organization organization = submission.getOrganization();
        if( organization == null ){
            // TODO handle error
        }

        List<Long> legislatorIds = parseCheckedLegislators(submission);
        Map<Long, VoteSide> voteSideMap = parseVoteSidesByBillIdMap(submission);
        ReportCard card = reportCardPersistence.updateReportCard(id, organization, voteSideMap, legislatorIds);

        HtmlLegupResponse response = HtmlLegupResponse.withLinks(this.getClass(), user, navLinks(id));
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

    private List<NavLink> navLinks(long reportCardId) {
        return Arrays.asList(
                new NavLink("Edit", "/legup/view_report_card_form?report_card_id=" + reportCardId),
                new NavLink("Calculate", "/legup/view_report_card_scores?report_card_id=" + reportCardId)

        );
    }
}
