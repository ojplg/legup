package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.LegislatorDao;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.illinois.MemberHtmlParser;
import org.center4racialjustice.legup.web.Handler;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SaveLegislators implements Handler {

    private ConnectionPool connectionPool;

    public SaveLegislators(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public VelocityContext handle(Request request, HttpServletResponse httpServletResponse)
    throws SQLException {
        String memberUrl = request.getParameter("url");

        MemberHtmlParser parser = MemberHtmlParser.load(memberUrl);
        List<Legislator> legislators = parser.getLegislators();

        Connection connection = connectionPool.getConnection();
        LegislatorDao dao = new LegislatorDao(connectionPool.session());
        for (Legislator leg : legislators) {
            dao.save(leg);
        }

        connection.close();

        VelocityContext vc = new VelocityContext();
        vc.put("saved_legislator_count", legislators.size());
        return vc;
    }
}
