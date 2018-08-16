package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.LegislatorDao;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.web.Handler;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ViewMembers implements Handler {

    private final ConnectionPool connectionPool;

    public ViewMembers(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public VelocityContext handle(Request request, HttpServletResponse httpServletResponse) throws IOException {
        VelocityContext vc = new VelocityContext();

        try {
            Connection connection = connectionPool.getConnection();
            LegislatorDao dao = new LegislatorDao(connection);
            List<Legislator> legislators = dao.readAll();
            vc.put("legislators", legislators);

            connection.close();

            return vc;

        } catch (SQLException ex){
            throw new RuntimeException(ex);
        }

    }

    @Override
    public String getTemplate() {
        return "view_members.vtl";
    }
}
