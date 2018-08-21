package org.center4racialjustice.legup.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.web.handlers.CollateBillVotes;
import org.center4racialjustice.legup.web.handlers.LoadBill;
import org.center4racialjustice.legup.web.handlers.ViewBillForm;
import org.center4racialjustice.legup.web.handlers.ViewLegislatorVotes;
import org.center4racialjustice.legup.web.handlers.ViewLegislators;
import org.center4racialjustice.legup.web.handlers.ViewParsedLegislators;
import org.center4racialjustice.legup.web.handlers.ViewFindLegislatorsForm;
import org.center4racialjustice.legup.web.handlers.SaveCollatedVotes;
import org.center4racialjustice.legup.web.handlers.SaveLegislators;
import org.center4racialjustice.legup.web.handlers.ViewBillVotes;
import org.center4racialjustice.legup.web.handlers.ViewBills;
import org.center4racialjustice.legup.web.handlers.ViewReportCards;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppHandler extends AbstractHandler {

    private static final Logger log = LogManager.getLogger(AppHandler.class);

    private final Map<String, RequestHandler> handlerMap = new HashMap<>();

    AppHandler(ConnectionPool connectionPool) {
        List<Handler> handlers = new ArrayList<>();

        handlers.add(new ViewBillForm());
        handlers.add(new ViewFindLegislatorsForm());
        handlers.add(new LoadBill());
        handlers.add(new ViewParsedLegislators());
        handlers.add(new SaveLegislators(connectionPool));
        handlers.add(new ViewLegislators(connectionPool));
        handlers.add(new CollateBillVotes(connectionPool));
        handlers.add(new SaveCollatedVotes(connectionPool));
        handlers.add(new ViewBills(connectionPool));
        handlers.add(new ViewBillVotes(connectionPool));
        handlers.add(new ViewLegislators(connectionPool));
        handlers.add(new ViewLegislatorVotes(connectionPool));
        handlers.add(new ViewReportCards(connectionPool));

        for (Handler handler : handlers) {
            RequestHandler requestHandler = new RequestHandler(handler);
            handlerMap.put(requestHandler.getRouteName(), requestHandler);
        }
    }

    @Override
    public void handle(String s, Request request, HttpServletRequest httpServletRequest,
                   HttpServletResponse httpServletResponse) throws IOException {

        String appPath = request.getPathInfo();

        log.info("Handling request to" + appPath);

        if( handlerMap.containsKey(appPath) ){
            RequestHandler requestHandler = handlerMap.get(appPath);
            requestHandler.processRequest(request, httpServletResponse);
        } else {
            log.warn("UNKNOWN APPLICATION PATH " + appPath);
        }

    }

}
