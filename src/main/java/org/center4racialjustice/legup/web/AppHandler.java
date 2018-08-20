package org.center4racialjustice.legup.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.web.handlers.CollateBillVotes;
import org.center4racialjustice.legup.web.handlers.LoadBill;
import org.center4racialjustice.legup.web.handlers.ViewBillForm;
import org.center4racialjustice.legup.web.handlers.ViewLegislators;
import org.center4racialjustice.legup.web.handlers.ViewParsedLegislators;
import org.center4racialjustice.legup.web.handlers.ViewFindLegislatorsForm;
import org.center4racialjustice.legup.web.handlers.SaveCollatedVotes;
import org.center4racialjustice.legup.web.handlers.SaveMembers;
import org.center4racialjustice.legup.web.handlers.ViewBillVotes;
import org.center4racialjustice.legup.web.handlers.ViewBills;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AppHandler extends AbstractHandler {

    private static final Logger log = LogManager.getLogger(AppHandler.class);

    private final Map<String, RequestHandler> handlers = new HashMap<>();

    AppHandler(ConnectionPool connectionPool){
        handlers.put("/view_bill_form", new RequestHandler(new ViewBillForm()));
        handlers.put("/view_find_legislators_form", new RequestHandler(new ViewFindLegislatorsForm()));
        handlers.put("/load_bill", new RequestHandler(new LoadBill()));
        handlers.put("/view_parsed_legislators", new RequestHandler(new ViewParsedLegislators()));
        handlers.put("/save_members", new RequestHandler(new SaveMembers(connectionPool)));
        handlers.put("/view_members", new RequestHandler(new ViewLegislators(connectionPool)));
        handlers.put("/collate_bill_votes", new RequestHandler(new CollateBillVotes(connectionPool)));
        handlers.put("/save_collated_votes", new RequestHandler(new SaveCollatedVotes(connectionPool)));
        handlers.put("/view_bills", new RequestHandler(new ViewBills(connectionPool)));
        handlers.put("/view_bill_votes", new RequestHandler(new ViewBillVotes(connectionPool)));
    }

    @Override
    public void handle(String s, Request request, HttpServletRequest httpServletRequest,
                   HttpServletResponse httpServletResponse) throws IOException {

        String appPath = request.getPathInfo();

        log.info("Handling request to" + appPath);

        if( handlers.containsKey(appPath) ){
            RequestHandler requestHandler = handlers.get(appPath);
            requestHandler.processRequest(request, httpServletResponse);
        } else {
            log.warn("UNKNOWN APPLICATION PATH " + appPath);
        }

    }

}
