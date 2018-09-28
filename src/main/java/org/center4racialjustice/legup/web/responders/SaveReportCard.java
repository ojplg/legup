package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.db.BillDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.ConnectionWrapper;
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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SaveReportCard implements Responder {

    private final ConnectionPool connectionPool;

    public SaveReportCard(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {

        try (ConnectionWrapper connection = connectionPool.getWrappedConnection()) {
            Long id = submission.getLongRequestParameter( "id");
            String name = submission.getParameter("name");
            long session = submission.getLongRequestParameter("session");

            ReportCardDao reportCardDao = new ReportCardDao(connection);

            ReportCard reportCard;
            if( id == null ){
                reportCard = new ReportCard();
                reportCard.setName(name);
                reportCard.setSessionNumber(session);

                long reportCardId = reportCardDao.save(reportCard);
                reportCard.setId(reportCardId);
            } else {
                reportCard = reportCardDao.read(id);

                reportCard.setName(name);
                reportCard.setSessionNumber(session);

                Map<Long, VoteSide> voteSideByBillIdMap = parseVoteSidesByBillIdMap(submission);
                BillDao billDao = new BillDao(connection);
                List<Bill> bills = billDao.readBySession(session);
                reportCard.resetReportFactorSettings(bills, voteSideByBillIdMap);

                LegislatorDao legislatorDao = new LegislatorDao(connection);
                List<Legislator> legislators = legislatorDao.readBySession(reportCard.getSessionNumber());
                List<Long> selectedLegislatorIds = parseCheckedLegislators(submission);
                reportCard.resetSelectedLegislators(legislators, selectedLegislatorIds);

                reportCardDao.save(reportCard);
            }

            LegupResponse response = new LegupResponse(this.getClass());
            response.putVelocityData("reportCard", reportCard);
            return response;
        }
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
