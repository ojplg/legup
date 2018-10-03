package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.db.BillDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.LegislatorDao;
import org.center4racialjustice.legup.db.ReportCardDao;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.domain.VoteSide;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaveReportCard implements Responder {

    private final ConnectionPool connectionPool;

    public SaveReportCard(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        Long id = submission.getLongRequestParameter( "id");
        String name = submission.getParameter("name");
        long session = submission.getLongRequestParameter("session");

        ReportCard reportCard = connectionPool.runAndCommit( connection ->
        {
            ReportCardDao reportCardDao = new ReportCardDao(connection);

            ReportCard card;
            if( id == null ){
                card = new ReportCard();
                card.setName(name);
                card.setSessionNumber(session);

                long reportCardId = reportCardDao.save(card);
                card.setId(reportCardId);
            } else {
                card = reportCardDao.read(id);

                card.setName(name);
                card.setSessionNumber(session);

                Map<Long, VoteSide> voteSideByBillIdMap = parseVoteSidesByBillIdMap(submission);
                BillDao billDao = new BillDao(connection);
                List<Bill> bills = billDao.readBySession(session);
                card.resetReportFactorSettings(bills, voteSideByBillIdMap);

                LegislatorDao legislatorDao = new LegislatorDao(connection);
                List<Legislator> legislators = legislatorDao.readBySession(card.getSessionNumber());
                List<Long> selectedLegislatorIds = parseCheckedLegislators(submission);
                card.resetSelectedLegislators(legislators, selectedLegislatorIds);

                reportCardDao.save(card);
            }
            return card;
        });

        LegupResponse response = new LegupResponse(this.getClass());
        response.putVelocityData("reportCard", reportCard);
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
}
