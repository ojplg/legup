package org.center4racialjustice.legup.web;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AppHandler extends AbstractHandler {

    @Override
    public void handle(String s, Request request, HttpServletRequest httpServletRequest,
                   HttpServletResponse httpServletResponse) throws IOException, ServletException {

        System.out.println("  GOT A REQUEST  !!! ");

    }

}
