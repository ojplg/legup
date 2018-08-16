package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.illinois.MemberHtmlParser;
import org.center4racialjustice.legup.web.Handler;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class LoadMembers implements Handler {
    @Override
    public VelocityContext handle(Request request, HttpServletResponse httpServletResponse) throws IOException {
        String memberUrl = request.getParameter("url");

        MemberHtmlParser parser = MemberHtmlParser.load(memberUrl);
        List<Legislator> legislators = parser.getNames();

        VelocityContext vc = new VelocityContext();
        vc.put("legislators", legislators);
        vc.put("url", memberUrl);

        return vc;
    }

    @Override
    public String getTemplate() {
        return "member_table.vtl";
    }
}
