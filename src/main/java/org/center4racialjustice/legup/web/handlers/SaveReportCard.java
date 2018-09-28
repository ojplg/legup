package org.center4racialjustice.legup.web.handlers;

import org.center4racialjustice.legup.db.BillDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.ConnectionWrapper;
import org.center4racialjustice.legup.db.LegislatorDao;
import org.center4racialjustice.legup.db.ReportCardDao;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.domain.ReportFactor;
import org.center4racialjustice.legup.domain.VoteSide;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;

import java.util.ArrayList;
import java.util.Enumeration;
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

        try (ConnectionWrapper connection = connectionPool.getWrappedConnection()) {
            Long id = submission.getLongRequestParameter( "id");
            String name = submission.getParameter("name");
            long session = submission.getLongRequestParameter("session");

            ReportCardDao reportCardDao = new ReportCardDao(connection);
            BillDao billDao = new BillDao(connection);

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
                List<ReportFactor> factorsToRemove = new ArrayList<>();

                //FIXME: Move code to ReportCard object and add tests
                for(ReportFactor reportFactor : reportCard.getReportFactors()){
                    Long billId = reportFactor.getBill().getId();
                    if( voteSideByBillIdMap.containsKey(billId)){
                        reportFactor.setVoteSide(voteSideByBillIdMap.get(billId));
                        voteSideByBillIdMap.remove(billId);
                    } else {
                        factorsToRemove.add(reportFactor);
                    }
                }
                for( ReportFactor reportFactor : factorsToRemove ){
                    reportCard.getReportFactors().remove(reportFactor);
                }
                for(Map.Entry<Long, VoteSide> billVotePair : voteSideByBillIdMap.entrySet()){
                    Bill bill = billDao.read(billVotePair.getKey());
                    ReportFactor factor = new ReportFactor();
                    factor.setVoteSide(billVotePair.getValue());
                    factor.setBill(bill);
                    reportCard.getReportFactors().add(factor);
                }

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
        String legislatorIdPrefix = "legislator_";
        List<Long> legislatorIds = new ArrayList<>();
        Enumeration<String> parameterNames = submission.getParameterNames();
        while(parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            if (parameterName.startsWith(legislatorIdPrefix)){
                String legislatorIdString = parameterName.substring(legislatorIdPrefix.length());
                Long legislatorId = Long.parseLong(legislatorIdString);
                legislatorIds.add(legislatorId);
            }
        }
        return legislatorIds;
    }

    private Map<Long, VoteSide> parseVoteSidesByBillIdMap(LegupSubmission submission){
        final String billVoteSidePrefix = "bill_vote_side_";
        Map<Long, VoteSide> voteSideByBillIdMap = new HashMap<>();
        Enumeration<String> parameterNames = submission.getParameterNames();
        while(parameterNames.hasMoreElements()){
            String parameterName = parameterNames.nextElement();
            if (parameterName.startsWith(billVoteSidePrefix)){
                String billNumberString = parameterName.substring(billVoteSidePrefix.length());
                Long billNumber = Long.parseLong(billNumberString);
                String voteString = submission.getParameter(parameterName);
                if ( voteString.equals("Yes") || voteString.equals("No")) {
                    VoteSide voteSide = VoteSide.fromCode(voteString.substring(0, 1));
                    voteSideByBillIdMap.put(billNumber, voteSide);
                }
            }
        }
        return voteSideByBillIdMap;
    }
}
