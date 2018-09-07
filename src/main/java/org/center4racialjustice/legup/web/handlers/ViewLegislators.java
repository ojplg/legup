package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.ConnectionWrapper;
import org.center4racialjustice.legup.db.LegislatorDao;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.web.Handler;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

public class ViewLegislators implements Handler {

    private final ConnectionPool connectionPool;

    public ViewLegislators(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public VelocityContext handle(Request request, HttpServletResponse httpServletResponse){

        try (ConnectionWrapper connection = connectionPool.getWrappedConnection()) {

            VelocityContext vc = new VelocityContext();

            LegislatorDao dao = new LegislatorDao(connection);
            List<Legislator> legislators = dao.readAll();
            Collections.sort(legislators);
            vc.put("legislators", legislators);

            return vc;
        }

    }
}
