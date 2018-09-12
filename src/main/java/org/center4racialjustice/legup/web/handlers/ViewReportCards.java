package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.ConnectionWrapper;
import org.center4racialjustice.legup.db.ReportCardDao;
import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.web.Handler;
import org.center4racialjustice.legup.web.LegupSession;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class ViewReportCards implements Handler {

    private final ConnectionPool connectionPool;

    public ViewReportCards(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    @Override
    public VelocityContext handle(Request request, LegupSession legupSession, HttpServletResponse httpServletResponse) {
        try (ConnectionWrapper connection = connectionPool.getWrappedConnection()){
            VelocityContext velocityContext = new VelocityContext();

            ReportCardDao reportCardDao = new ReportCardDao(connection);
            List<ReportCard> reportCards = reportCardDao.readAll();

            velocityContext.put("report_cards", reportCards);

            return velocityContext;
        }
    }
}
