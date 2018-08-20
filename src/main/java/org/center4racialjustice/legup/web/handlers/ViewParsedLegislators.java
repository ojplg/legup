package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.illinois.MemberHtmlParser;
import org.center4racialjustice.legup.web.Handler;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class ViewParsedLegislators implements Handler {
    @Override
    public VelocityContext handle(Request request, HttpServletResponse httpServletResponse) {
        String memberUrl = request.getParameter("url");

        MemberHtmlParser parser = MemberHtmlParser.load(memberUrl);
        List<Legislator> legislators = parser.getLegislators();

        VelocityContext vc = new VelocityContext();
        vc.put("legislators", legislators);
        vc.put("url", memberUrl);

        return vc;
    }
}
