package org.center4racialjustice.legup.web.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.db.BillDao;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.ReportCardDao;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.domain.ReportFactor;
import org.center4racialjustice.legup.util.Lists;
import org.center4racialjustice.legup.web.Handler;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewReportCardForm implements Handler {

    private static final Logger log = LogManager.getLogger(ViewReportCardForm.class);

    private final ConnectionPool connectionPool;

    public ViewReportCardForm(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    @Override
    public VelocityContext handle(Request request, HttpServletResponse httpServletResponse) throws IOException, SQLException {
        Connection connection = connectionPool.getConnection();
        try {
            VelocityContext velocityContext = new VelocityContext();

            String idString = request.getParameter("report_card_id");
            if( idString != null && idString.length() > 0 ) {
                long id = Long.parseLong(idString);

                ReportCardDao reportCardDao = new ReportCardDao(connection);
                ReportCard reportCard = reportCardDao.read(id);
                velocityContext.put("report_card", reportCard);

                Long sessionId = reportCard.getSessionNumber();

                BillDao billDao = new BillDao(connection);
                List<Bill> bills = billDao.readBySession(sessionId);

                log.info("Found " + bills.size() + " in session " + sessionId);

                List<ReportFactor> factors = reportCard.getReportFactors();
                Map<Long, ReportFactor> factorsByBillId = Lists.asMap(factors, f -> f.getBill().getId());

                Map<Bill, String> factorSettings = new HashMap<>();

                for(Bill bill : bills){
                    ReportFactor matchingFactor = factorsByBillId.get(bill.getId());
                    if ( matchingFactor == null ){
                        factorSettings.put(bill, "Unselected");
                    } else {
                        factorSettings.put(bill, matchingFactor.getVoteSide().getCode());
                    }

                }
               velocityContext.put("factor_settings", factorSettings);
            }

            return velocityContext;
        } finally {
            connection.close();
        }
    }
}
