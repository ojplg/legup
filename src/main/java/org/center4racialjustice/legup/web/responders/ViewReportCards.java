package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.DaoBuilders;
import org.center4racialjustice.legup.db.ReportCardDao;
import org.center4racialjustice.legup.domain.Organization;
import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;
import org.hrorm.Dao;

import java.util.ArrayList;
import java.util.List;

public class ViewReportCards implements Responder {

    private final ConnectionPool connectionPool;

    public ViewReportCards(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        return connectionPool.useConnection(connection -> {
            Dao<Organization> organizationDao = DaoBuilders.ORGANIZATIONS.buildDao(connection);

            List<Organization> organizations = organizationDao.selectAll();
            List<ReportCard> reportCards = new ArrayList<>();

            for( Organization organization : organizations ) {
                reportCards.addAll(organization.getReportCards());
            }

            for( ReportCard card : reportCards ){
                System.out.println("CARD " + card);
            }

            HtmlLegupResponse response = HtmlLegupResponse.withHelp(this.getClass(), submission.getLoggedInUser());
            response.putVelocityData("report_cards", reportCards);
            return response;
        });
    }

}
