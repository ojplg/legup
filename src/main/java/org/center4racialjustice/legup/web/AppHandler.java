package org.center4racialjustice.legup.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.NameParser;
import org.center4racialjustice.legup.web.responders.*;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/*
   Every page should have
   1. Title of app/link to home page
   2. Stateful link for login/logout
   3. Space for link to help page (and link if available)
   4. Space for relevant navigations (and links if available)

   Design principles
   1. Page templates should have minimum necessary content
   2. Nice borders for content
   3. No borders for headers
 */

public class AppHandler extends AbstractHandler {

    private static final Logger log = LogManager.getLogger(AppHandler.class);
    private static final Logger requestLog = LogManager.getLogger("request");

    private final Map<String, Responder> responderMap = new HashMap<>();

    AppHandler(ConnectionPool connectionPool, NameParser nameParser) {
        List<Responder> responders = new ArrayList<>();

        responders.add(new ViewMain(connectionPool));
        responders.add(new ViewBillForm());
        responders.add(new ViewFindLegislatorsForm());
        responders.add(new ViewParsedLegislators(connectionPool, nameParser));
        responders.add(new SaveLegislators(connectionPool));
        responders.add(new ViewLegislatorSessions(connectionPool));
        responders.add(new ViewLegislators(connectionPool));
        responders.add(new ViewBills(connectionPool));
        responders.add(new ViewBillVotes(connectionPool));
        responders.add(new ViewLegislatorVotes(connectionPool));
        responders.add(new ViewReportCardForm(connectionPool));
        responders.add(new SaveReportCard(connectionPool));
        responders.add(new ViewReportCardScores(connectionPool));
        responders.add(new ViewBillSearchForm());
        responders.add(new ViewBillSearchResults(connectionPool, nameParser));
        responders.add(new SaveSearchedBill(connectionPool));
        responders.add(new ViewBillSponsors(connectionPool));
        responders.add(new ViewReportCardBills());
        responders.add(new ViewReportCardBill());
        responders.add(new ViewReportCardLegislator());
        responders.add(new ViewBillDataTable(connectionPool));
        responders.add(new ViewBillDataCsv(connectionPool));
        responders.add(new ViewReportCardScoresCsv());
        responders.add(new ViewNewReportCardForm(connectionPool));
        responders.add(new SaveNewReportCard(connectionPool));
        responders.add(new SaveNewUser(connectionPool));
        responders.add(new UserLogin(connectionPool));
        responders.add(new UserLogout());
        responders.add(new ViewLogin());
        responders.add(new ViewUserProfile(connectionPool));
        responders.add(new ViewOrganization(connectionPool));
        responders.add(new ViewResetPassword(connectionPool));
        responders.add(new SaveResetPassword(connectionPool));
        responders.add(new ViewLegislatorForm(connectionPool));
        responders.add(new SaveLegislator(connectionPool));
        responders.add(new ViewBillLoadData(connectionPool));
        responders.add(new ViewUsers(connectionPool));
        responders.add(new ViewOrganizations(connectionPool));
        responders.add(new ViewParsedCommittees(connectionPool, nameParser));
        responders.add(new SaveCommittees(connectionPool));
        responders.add(new ViewLegislatorCommittees(connectionPool));
        responders.add(new ViewCommittees(connectionPool));
        responders.add(new ViewCommittee(connectionPool));
        responders.add(new ViewBillHistory(connectionPool));
        responders.add(new HardReloadBill(connectionPool, nameParser));

        for (Responder responder : responders) {
            String routeName = "/" + Util.classNameToLowercaseWithUnderlines(responder.getClass());
            log.info("Setting responder for " + routeName);
            responderMap.put(routeName, responder);
        }
    }

    @Override
    public void handle(String s, Request request, HttpServletRequest httpServletRequest,
                   HttpServletResponse httpServletResponse) {
        try {
            requestLog.info(request.getOriginalURI());

            String appPath = request.getPathInfo();

            if (responderMap.containsKey(appPath)) {
                runResponder(request, httpServletResponse);
            } else if ( appPath.startsWith("/help") ) {
                log.info("serving help for " + appPath);
                doHelpRequest(appPath, httpServletResponse);
                request.setHandled(true);
            } else {
                log.info("Request for resource handler: " + appPath);
            }
        } catch (Exception ex){
            log.error("Exception in request processing", ex);
            throw new RuntimeException(ex);
        }
    }

    private void doHelpRequest(String appPath, HttpServletResponse httpServletResponse)
    throws IOException {
        String helpTemplate = appPath.substring(5);
        log.info("From appPath " + appPath + " help template " + helpTemplate);
        Writer writer = httpServletResponse.getWriter();
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("helpTemplate", "/templates/help/" + helpTemplate + ".vtl");
        Velocity.mergeTemplate("/templates/help/container.vtl", "ISO-8859-1", velocityContext, writer);
    }

    private LegupSubmission instantiateSubmission(Request request){
        HttpSession session = request.getSession();
        LegupSession legupSession = (LegupSession) session.getAttribute("LegupSession");
        if (legupSession == null){
            legupSession = new LegupSession();
            session.setAttribute("LegupSession", legupSession);
        }
        int count = legupSession.increment();
        log.info("Session count " + count);
        return new LegupSubmission(legupSession, request);
    }

    private void runResponder(Request request, HttpServletResponse httpServletResponse)
    throws IOException {

        String appPath = request.getPathInfo();
        Responder responder = responderMap.get(appPath);
        LegupSubmission legupSubmission = instantiateSubmission(request);

        LegupResponse legupResponse = new ContinueLegupResponse(responder.getClass(), legupSubmission.getParameters());

        while( ! legupResponse.shouldRender() ){
            log.info("Handling request to " + appPath + " with " + responder.getClass().getSimpleName());
            String nextKey = legupResponse.actionKey();
            log.info("Forwarded response to " + nextKey);
            responder = responderMap.get(nextKey);
            legupSubmission = legupSubmission.update(legupResponse.getParameters());

            // check for logged in if necessary and redirect if not
            if ( responder.isSecured() && ! legupSubmission.isLoggedIn() ){
                log.info("Request denied due to no logged in user");
                responder = responderMap.get("/view_login");
            } else if ( responder.isSecured() && (!responder.permitted(legupSubmission)) && (!legupSubmission.isSuperUserRequest()) ){
                // TODO: Better to redirect to some page with an explanation
                log.info("Request denied due lacking permission");
                responder = responderMap.get("/view_user_profile");
            } else {
                legupResponse = responder.handle(legupSubmission);
            }
        }

        String templatePath = "/templates/" + legupResponse.actionKey();
        log.info("Rendering response with template " + templatePath);
        httpServletResponse.setHeader("Content-Type", legupResponse.getContentType());

        Writer writer = httpServletResponse.getWriter();
        VelocityContext velocityContext = legupResponse.getVelocityContext();

        if( legupSubmission.isSuperUserRequest() ){
            log.info("Super user: " + legupSubmission.getLoggedInUser());
            velocityContext.put("isSuperUser", true);
        }

        if (legupResponse.useContainer()) {
            velocityContext.put("contents", templatePath);
            Velocity.mergeTemplate("/templates/container.vtl", "ISO-8859-1", velocityContext, writer);
        } else {
            Velocity.mergeTemplate(templatePath, "ISO-8859-1", velocityContext, writer);
        }
        request.setHandled(true);
    }
}
