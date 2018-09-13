package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.ConnectionWrapper;
import org.center4racialjustice.legup.db.LegislatorDao;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.illinois.MemberHtmlParser;
import org.center4racialjustice.legup.web.Handler;
import org.center4racialjustice.legup.web.LegupSession;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class SaveLegislators implements Handler {

    private ConnectionPool connectionPool;

    public SaveLegislators(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public VelocityContext handle(Request request, LegupSession legupSession, HttpServletResponse httpServletResponse) {

        try (ConnectionWrapper connection = connectionPool.getWrappedConnection()) {

            String oneTimeKey = request.getParameter("oneTimeKey");

            MemberHtmlParser parser = (MemberHtmlParser) legupSession.getObject(LegupSession.MemberHtmlParserKey, oneTimeKey);

            VelocityContext vc = new VelocityContext();

            if( parser != null ) {
                List<Legislator> legislators = parser.getLegislators();

                LegislatorDao dao = new LegislatorDao(connection);
                for (Legislator leg : legislators) {
                    dao.save(leg);
                }
                vc.put("saved_legislator_count", legislators.size());
            } else {
                // FIXME: Actually, this should do something else.
                vc.put("saved_legislator_count", 0);
            }
            return vc;
        }
    }
}
