package org.center4racialjustice.legup.web;

import org.center4racialjustice.legup.db.ConnectionPool;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

public class ServerStarter {

    private final int port = 8000;
    private final ConnectionPool connectionPool;

    public ServerStarter(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    public void start() throws Exception {
        Server server = new Server(port);

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[]{"html/index.html"});
        resourceHandler.setResourceBase("target/classes");

        ContextHandler appHandler = new ContextHandler();
        appHandler.setContextPath("/app");
        appHandler.setHandler(new AppHandler(connectionPool));

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{appHandler, resourceHandler});

        server.setHandler(handlers);

        server.start();
    }
}
