package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.service.LegislatorPersistence;
import org.center4racialjustice.legup.web.Handler;
import org.center4racialjustice.legup.web.LegupSession;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class SaveLegislators implements Handler {

    private LegislatorPersistence legislatorPersistence;

    public SaveLegislators(ConnectionPool connectionPool) {
        this.legislatorPersistence = new LegislatorPersistence(connectionPool);
    }

    @Override
    public VelocityContext handle(Request request, LegupSession legupSession, HttpServletResponse httpServletResponse) {

        String oneTimeKey = request.getParameter("oneTimeKey");

        List<Legislator> unknownLegislators = (List<Legislator>) legupSession.getObject(LegupSession.UnknownLegislatorsKey, oneTimeKey);

        VelocityContext vc = new VelocityContext();

        if( unknownLegislators != null ) {
            int savedCount = legislatorPersistence.insertLegislators(unknownLegislators);
            vc.put("saved_legislator_count", savedCount);
        } else {
            // FIXME: Actually, this should do something else.
            // Display an error or something
            vc.put("saved_legislator_count", 0);
        }
        return vc;
    }
}
