package org.center4racialjustice.legup.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.web.handlers.CollateBillVotes;
import org.center4racialjustice.legup.web.handlers.LoadBill;
import org.center4racialjustice.legup.web.handlers.LoadMembers;
import org.center4racialjustice.legup.web.handlers.RenderLoadBillFormPage;
import org.center4racialjustice.legup.web.handlers.RenderLocateMembersPage;
import org.center4racialjustice.legup.web.handlers.SaveMembers;
import org.center4racialjustice.legup.web.handlers.ViewMembers;
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
        handlers.put("/load_bill_form", new RequestHandler(new RenderLoadBillFormPage()));
        handlers.put("/locate_members_form", new RequestHandler(new RenderLocateMembersPage()));
        handlers.put("/load_bill", new RequestHandler(new LoadBill()));
        handlers.put("/load_members", new RequestHandler(new LoadMembers()));
        handlers.put("/save_members", new RequestHandler(new SaveMembers(connectionPool)));
        handlers.put("/view_members", new RequestHandler(new ViewMembers(connectionPool)));
        handlers.put("/collate_bill_votes", new RequestHandler(new CollateBillVotes(connectionPool)));
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
