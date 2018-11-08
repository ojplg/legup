package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.ReportCardDao;
import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;
import org.center4racialjustice.legup.web.Util;

import java.util.List;

public class ViewReportCards implements Responder {

    private final ConnectionPool connectionPool;

    public ViewReportCards(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        return connectionPool.useConnection(connection -> {
            ReportCardDao reportCardDao = new ReportCardDao(connection);
            List<ReportCard> reportCards = reportCardDao.readAll();

            HtmlLegupResponse response = new HtmlLegupResponse(this.getClass());
            response.putVelocityData("report_cards", reportCards);
            return response;
        });
    }

    @Override
    public String helpLink() {
        return "/legup/help/" + Util.classNameToLowercaseWithUnderlines(ViewReportCards.class);
    }
}
