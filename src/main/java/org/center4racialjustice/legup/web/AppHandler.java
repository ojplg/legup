package org.center4racialjustice.legup.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.NameParser;
import org.center4racialjustice.legup.web.handlers.SaveReportCard;
import org.center4racialjustice.legup.web.handlers.SaveSearchedBill;
import org.center4racialjustice.legup.web.handlers.ViewBillForm;
import org.center4racialjustice.legup.web.handlers.ViewBillSearchForm;
import org.center4racialjustice.legup.web.handlers.ViewBillSearchResults;
import org.center4racialjustice.legup.web.handlers.ViewBillSponsors;
import org.center4racialjustice.legup.web.handlers.ViewLegislatorVotes;
import org.center4racialjustice.legup.web.handlers.ViewLegislators;
import org.center4racialjustice.legup.web.handlers.ViewParsedLegislators;
import org.center4racialjustice.legup.web.handlers.ViewFindLegislatorsForm;
import org.center4racialjustice.legup.web.handlers.SaveLegislators;
import org.center4racialjustice.legup.web.handlers.ViewBillVotes;
import org.center4racialjustice.legup.web.handlers.ViewBills;
import org.center4racialjustice.legup.web.handlers.ViewReportCardBill;
import org.center4racialjustice.legup.web.handlers.ViewReportCardBills;
import org.center4racialjustice.legup.web.handlers.ViewReportCardForm;
import org.center4racialjustice.legup.web.handlers.ViewReportCardLegislator;
import org.center4racialjustice.legup.web.handlers.ViewReportCardScores;
import org.center4racialjustice.legup.web.handlers.ViewReportCards;
import org.center4racialjustice.legup.web.handlers.ViewBillDataCsv;
import org.center4racialjustice.legup.web.handlers.ViewBillDataTable;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppHandler extends AbstractHandler {

    private static final Logger log = LogManager.getLogger(AppHandler.class);

    private final Map<String, Responder> responderMap = new HashMap<>();

    AppHandler(ConnectionPool connectionPool, NameParser nameParser) {
        List<Responder> responders = new ArrayList<>();

        responders.add(new ViewBillForm());
        responders.add(new ViewFindLegislatorsForm());
        responders.add(new ViewParsedLegislators(connectionPool, nameParser));
        responders.add(new SaveLegislators(connectionPool));
        responders.add(new ViewLegislators(connectionPool));
        responders.add(new ViewBills(connectionPool));
        responders.add(new ViewBillVotes(connectionPool));
        responders.add(new ViewLegislatorVotes(connectionPool));
        responders.add(new ViewReportCards(connectionPool));
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

        for (Responder responder : responders) {
            String routeName = "/" + Util.classNameToLowercaseWithUnderlines(responder.getClass());
            log.info("Setting responder for " + routeName);
            responderMap.put(routeName, responder);
        }
    }

    @Override
    public void handle(String s, Request request, HttpServletRequest httpServletRequest,
                   HttpServletResponse httpServletResponse) {

        String appPath = request.getPathInfo();

        log.info("Handling request to " + appPath);

        if ( responderMap.containsKey(appPath) ) {
            HttpSession session = request.getSession();
            LegupSession legupSession = doSession(session);
            LegupSubmission legupSubmission = new LegupSubmission(legupSession, request);
            Responder responder = responderMap.get(appPath);
            LegupResponse legupResponse = responder.handle(legupSubmission);
            processResponse(legupResponse, httpServletResponse);
            request.setHandled(true);
        } else {
            log.info("Request for resource handler: " + appPath);
        }
    }

    private LegupSession doSession(HttpSession session){
        LegupSession legupSession = (LegupSession) session.getAttribute("LegupSession");
        if (legupSession == null){
            legupSession = new LegupSession();
            session.setAttribute("LegupSession", legupSession);
        }
        int count = legupSession.increment();
        log.info("Session count " + count);
        return legupSession;
    }

    private void processResponse(LegupResponse legupResponse, HttpServletResponse httpServletResponse) {
        try {
            log.info("Processed request with " + legupResponse.getResponderClassName());
            String templatePath = "/templates/" + legupResponse.getTemplateName();
            Writer writer = httpServletResponse.getWriter();
            VelocityContext velocityContext = legupResponse.getVelocityContext();
            if (legupResponse.useContainer()) {
                velocityContext.put("contents", templatePath);
                Velocity.mergeTemplate("/templates/container.vtl", "ISO-8859-1", velocityContext, writer);
            } else {
                Velocity.mergeTemplate(templatePath, "ISO-8859-1", velocityContext, writer);
            }
        } catch (Exception ex) {
            log.error("Exception in request processing", ex);
            throw new RuntimeException(ex);
        }
    }

}
