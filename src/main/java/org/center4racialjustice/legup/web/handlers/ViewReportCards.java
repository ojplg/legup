package org.center4racialjustice.legup.web.handlers;

import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.ConnectionWrapper;
import org.center4racialjustice.legup.db.ReportCardDao;
import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;

import java.util.List;

public class ViewReportCards implements Responder {

    private final ConnectionPool connectionPool;

    public ViewReportCards(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        try (ConnectionWrapper connection = connectionPool.getWrappedConnection()){
            ReportCardDao reportCardDao = new ReportCardDao(connection);
            List<ReportCard> reportCards = reportCardDao.readAll();

            LegupResponse response = new LegupResponse(this.getClass());
            response.putVelocityData("report_cards", reportCards);
            return response;
        }
    }
}
