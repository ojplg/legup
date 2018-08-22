package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.db.BillDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.ReportCardDao;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.domain.ReportFactor;
import org.center4racialjustice.legup.domain.VoteSide;
import org.center4racialjustice.legup.web.Handler;
import org.center4racialjustice.legup.web.Util;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class SaveReportCard implements Handler {

    private final ConnectionPool connectionPool;

    public SaveReportCard(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    @Override
    public VelocityContext handle(Request request, HttpServletResponse httpServletResponse) throws IOException, SQLException {
        Connection connection = connectionPool.getConnection();

        try {
            Long id = Util.getLongParameter(request, "id");
            String name = request.getParameter("name");
            String sessionString = request.getParameter("session");
            long session = Long.parseLong(sessionString);

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

                Map<Long, VoteSide> voteSideByBillIdMap = parseVoteSidesByBillIdMap(request);
                for(ReportFactor reportFactor : reportCard.getReportFactors()){
                    Long billId = reportFactor.getBill().getId();
                    if( voteSideByBillIdMap.containsKey(billId)){
                        reportFactor.setVoteSide(voteSideByBillIdMap.get(billId));
                        voteSideByBillIdMap.remove(billId);
                    } else {
                        // need to remove!
                        // IS THIS SAFE?
                        reportCard.getReportFactors().remove(reportFactor);
                    }
                }
                for(Map.Entry<Long, VoteSide> billVotePair : voteSideByBillIdMap.entrySet()){
                    Bill bill = billDao.read(billVotePair.getKey());
                    ReportFactor factor = new ReportFactor();
                    factor.setVoteSide(billVotePair.getValue());
                    factor.setBill(bill);
                    reportCard.getReportFactors().add(factor);
                }

                reportCardDao.save(reportCard);
            }

            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("reportCardId", reportCard.getId());
            return velocityContext;
        } finally {
            connection.close();
        }
    }

    private Map<Long, VoteSide> parseVoteSidesByBillIdMap(Request request){
        Map<Long, VoteSide> voteSideByBillIdMap = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while(parameterNames.hasMoreElements()){
            String parameterName = parameterNames.nextElement();
            final String billVoteSidePrefix = "bill_vote_side_";
            if (parameterName.startsWith(billVoteSidePrefix)){
                String billNumberString = parameterName.substring(billVoteSidePrefix.length());
                Long billNumber = Long.parseLong(billNumberString);
                String voteString = request.getParameter(parameterName);
                if ( voteString.equals("Yes") || voteString.equals("No")) {
                    VoteSide voteSide = VoteSide.fromCode(voteString.substring(0, 1));
                    voteSideByBillIdMap.put(billNumber, voteSide);
                }
            }
        }
        return voteSideByBillIdMap;
    }
}
