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
import java.util.List;

public class ViewReportCards implements Handler {

    private final ConnectionPool connectionPool;

    public ViewReportCards(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    @Override
    public VelocityContext handle(Request request, HttpServletResponse httpServletResponse) throws IOException, SQLException {
        Connection connection = connectionPool.getConnection();

        try {
            VelocityContext velocityContext = new VelocityContext();

            ReportCardDao reportCardDao = new ReportCardDao(connection);
            List<ReportCard> reportCards = reportCardDao.readAll();

            velocityContext.put("reportCards", reportCards);



            return velocityContext;
        } finally {
            connection.close();
        }

    }
}
