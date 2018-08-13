package org.center4racialjustice.legup.web;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;

public class ServerStarter {

    private final int port = 8000;

    public void start() throws Exception {
        Server server = new Server(port);

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[]{ "html/index.html"});
        resourceHandler.setResourceBase("target/classes");

        server.setHandler(resourceHandler);

        server.start();
    }

}
