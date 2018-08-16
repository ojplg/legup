package org.center4racialjustice.legup.web;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.LegislatorDao;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.illinois.BillVotes;
import org.center4racialjustice.legup.illinois.MemberHtmlParser;
import org.center4racialjustice.legup.illinois.Parser;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AppHandler extends AbstractHandler {

    @Override
    public void handle(String s, Request request, HttpServletRequest httpServletRequest,
                   HttpServletResponse httpServletResponse) throws IOException {

        String appPath = request.getPathInfo();

        System.out.println("HANDLING !!" + appPath);

        switch(appPath){
            case "/locate_members_form" :
                renderLocateMembersPage(request, httpServletResponse);
                break;
            case "/load_members" :
                doLoadMembers(request, httpServletResponse);
                break;
            case "/save_members" :
                doSaveMembers(request, httpServletResponse);
                break;
            case "/load_bill_form" :
                renderLoadBillFormPage(request, httpServletResponse);
                break;
            case "/load_bill" :
                doLoadBill(request, httpServletResponse);
                break;
        }

    }

    private void renderLoadBillFormPage(Request request, HttpServletResponse response)
            throws IOException {

        System.out.println("Doing load bill form page");

        VelocityContext vc = new VelocityContext();
        renderVelocityTemplate("/templates/load_bill_form.vtl", vc, response.getWriter());
        request.setHandled(true);
    }


    private void renderLocateMembersPage(Request request, HttpServletResponse response)
            throws IOException {

        System.out.println("Doing chooser form");

        VelocityContext vc = new VelocityContext();
        renderVelocityTemplate("/templates/locate_members_form.vtl", vc, response.getWriter());
        request.setHandled(true);
    }

    private void doLoadBill(Request request, HttpServletResponse response) throws IOException {
        String billUrl = request.getParameter("url");
        String contents = Parser.readFileFromUrl(billUrl);
        BillVotes votes = Parser.parseFileContents(contents);

        VelocityContext vc = new VelocityContext();
        vc.put("yeas", votes.getYeas());
        vc.put("nays", votes.getNays());
        vc.put("presents", votes.getPresents());
        vc.put("notVotings", votes.getNotVotings());

        renderVelocityTemplate("/templates/bill_votes_page.vtl", vc, response.getWriter());
        request.setHandled(true);
    }

    private void doLoadMembers(Request request, HttpServletResponse response)
    throws IOException {
        System.out.println("Doing members load");
        String memberUrl = request.getParameter("url");

        MemberHtmlParser parser = MemberHtmlParser.load(memberUrl);
        List<Legislator> legislators = parser.getNames();

        VelocityContext vc = new VelocityContext();
        vc.put("legislators", legislators);
        vc.put("url", memberUrl);

        renderVelocityTemplate("/templates/member_table.vtl", vc, response.getWriter());
        request.setHandled(true);
    }

    private void doSaveMembers(Request request, HttpServletResponse response)
    throws IOException {
        System.out.println("Doing member save");
        String memberUrl = request.getParameter("url");

        MemberHtmlParser parser = MemberHtmlParser.load(memberUrl);
        List<Legislator> legislators = parser.getNames();

        // do save here
        try {
            Connection connection = ConnectionPool.getConnection();
            LegislatorDao dao = new LegislatorDao(connection);
            for (Legislator leg : legislators) {
                dao.save(org.center4racialjustice.legup.db.Legislator.fromDomainLegislator(leg));
            }

            connection.close();
        } catch (SQLException ex){
            throw new RuntimeException(ex);
        }

        VelocityContext vc = new VelocityContext();
        vc.put("saved_member_count", legislators.size());
        renderVelocityTemplate("/templates/member_save_results.vtl", vc, response.getWriter());
        request.setHandled(true);
    }

    private void renderVelocityTemplate(String templatePath, VelocityContext vc, Writer writer){
        Velocity.init();

        InputStream in = this.getClass().getResourceAsStream(templatePath);

        Velocity.evaluate(vc, writer , "", new InputStreamReader(in));
    }

}
