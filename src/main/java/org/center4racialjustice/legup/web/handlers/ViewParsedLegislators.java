package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.illinois.MemberHtmlParser;
import org.center4racialjustice.legup.service.LegislatorPersistence;
import org.center4racialjustice.legup.web.Handler;
import org.center4racialjustice.legup.web.LegupSession;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class ViewParsedLegislators implements Handler {

    private final LegislatorPersistence legislatorPersistence;

    public ViewParsedLegislators(ConnectionPool connectionPool) {
        this.legislatorPersistence = new LegislatorPersistence(connectionPool);
    }

    @Override
    public VelocityContext handle(Request request, LegupSession legupSession, HttpServletResponse httpServletResponse) {
        String memberUrl = request.getParameter("url");

        MemberHtmlParser parser = MemberHtmlParser.load(memberUrl);
        List<Legislator> legislators = parser.getLegislators();
        List<Legislator> unknownLegislators = legislatorPersistence.filterOutSavedLegislators(legislators);

        String oneTimeKey = legupSession.setObject(LegupSession.UnknownLegislatorsKey, unknownLegislators);

        VelocityContext vc = new VelocityContext();
        vc.put("legislators", unknownLegislators);
        vc.put("parsedLegislatorCount", legislators.size());
        vc.put("oneTimeKey", oneTimeKey);

        return vc;
    }
}
