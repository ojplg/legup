package org.center4racialjustice.legup.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppHandler extends AbstractHandler {

    private static final Logger log = LogManager.getLogger(AppHandler.class);

    private final Map<String, RequestHandler> handlerMap = new HashMap<>();

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

        handlers.add(new ResponderHandler(new ViewBillDataTable(connectionPool)));
        handlers.add(new ResponderHandler(new ViewBillDataCsv(connectionPool)));

        for (Handler handler : handlers) {
            RequestHandler requestHandler = new RequestHandler(handler);
            String routeName = requestHandler.getRouteName();
            log.info("Setting handler for " + routeName);
            handlerMap.put(routeName, requestHandler);
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
}
