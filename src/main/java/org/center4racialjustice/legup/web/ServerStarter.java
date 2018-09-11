package org.center4racialjustice.legup.web;

import org.center4racialjustice.legup.db.ConnectionPool;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SessionIdManager;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.eclipse.jetty.server.session.SessionHandler;

public class ServerStarter {

    private final int port = 8000;
    private final ConnectionPool connectionPool;

    public ServerStarter(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    public void start() throws Exception {
        Server server = new Server(port);

        SessionIdManager idManager = new DefaultSessionIdManager(server);
        server.setSessionIdManager(idManager);

        SessionHandler sessionHandler = new SessionHandler();

        // NOTE: The resource handler is added to the handler mix
        // twice. Once under the /legup, and once at the root.
        // This allows static content to be served and paths
        // to be correct when running either standalone on the servlet
        // port, or when redirecting from host:80/legup via a
        // web server.
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setWelcomeFiles(new String[]{"html/index.html"});
        resourceHandler.setResourceBase("target/classes");

        ContextHandler appHandler = new ContextHandler();
        appHandler.setContextPath("/legup");
        HandlerList appHandlers = new HandlerList();
        AppHandler legUpHandler = new AppHandler(connectionPool);
        appHandlers.setHandlers(new Handler[]{ sessionHandler, legUpHandler, resourceHandler });
        appHandler.setHandler(appHandlers);

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{appHandler, resourceHandler });

        server.setHandler(handlers);

        server.start();
    }
}
