package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.ReportCardDao;
import org.center4racialjustice.legup.domain.ReportCard;
import org.center4racialjustice.legup.web.Handler;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class ViewReportCardForm implements Handler {

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
            }

            return velocityContext;
        } finally {
            connection.close();
        }
    }
}
