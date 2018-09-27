package org.center4racialjustice.legup.web;

import org.apache.http.HttpEntity;
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
import org.center4racialjustice.legup.web.responders.ViewBillDataCsv;
import org.center4racialjustice.legup.web.responders.ViewBillDataTable;
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

    private final Map<String, RequestHandler> handlerMap = new HashMap<>();
    private final Map<String, Responder> responderMap = new HashMap<>();

    AppHandler(ConnectionPool connectionPool, NameParser nameParser) {
        List<Handler> handlers = new ArrayList<>();

        handlers.add(new ViewBillForm());
        handlers.add(new ViewFindLegislatorsForm());
        handlers.add(new ViewParsedLegislators(connectionPool, nameParser));
        handlers.add(new SaveLegislators(connectionPool));
        handlers.add(new ViewLegislators(connectionPool));
        handlers.add(new ViewBills(connectionPool));
        handlers.add(new ViewBillVotes(connectionPool));
        handlers.add(new ViewLegislators(connectionPool));
        handlers.add(new ViewLegislatorVotes(connectionPool));
        handlers.add(new ViewReportCards(connectionPool));
        handlers.add(new ViewReportCardForm(connectionPool));
        handlers.add(new SaveReportCard(connectionPool));
        handlers.add(new ViewReportCardScores(connectionPool));
        handlers.add(new ViewBillSearchForm());
        handlers.add(new ViewBillSearchResults(connectionPool, nameParser));
        handlers.add(new SaveSearchedBill(connectionPool));
        handlers.add(new ViewBillSponsors(connectionPool));
        handlers.add(new ViewReportCardBills());
        handlers.add(new ViewReportCardBill());
        handlers.add(new ViewReportCardLegislator());

//        handlers.add(new ResponderHandler(new ViewBillDataTable(connectionPool)));
//        handlers.add(new ResponderHandler(new ViewBillDataCsv(connectionPool)));

        for (Handler handler : handlers) {
            RequestHandler requestHandler = new RequestHandler(handler);
            String routeName = requestHandler.getRouteName();
            log.info("Setting handler for " + routeName);
            handlerMap.put(routeName, requestHandler);
        }

        List<Responder> responders = new ArrayList<>();

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

        if( handlerMap.containsKey(appPath) ){
            HttpSession session = request.getSession();
            LegupSession legupSession = doSession(session);

            RequestHandler requestHandler = handlerMap.get(appPath);
            requestHandler.processRequest(request, legupSession, httpServletResponse);
        } else if ( responderMap.containsKey(appPath) ) {
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
