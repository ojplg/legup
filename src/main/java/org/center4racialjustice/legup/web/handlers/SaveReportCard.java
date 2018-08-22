package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.ReportCardDao;
import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.web.Handler;
import org.center4racialjustice.legup.web.Util;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

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

            ReportCard reportCard = new ReportCard();

            reportCard.setId(id);
            reportCard.setName(name);
            reportCard.setSessionNumber(session);

            ReportCardDao reportCardDao = new ReportCardDao(connection);
            long reportCardId = reportCardDao.save(reportCard);

            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("reportCardId", reportCardId);
            return velocityContext;
        } finally {
            connection.close();
        }
    }
}
