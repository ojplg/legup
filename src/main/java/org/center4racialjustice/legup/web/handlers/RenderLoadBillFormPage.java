package org.center4racialjustice.legup.web.handlers;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.web.Handler;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RenderLoadBillFormPage implements Handler {

    public VelocityContext handle(Request request, HttpServletResponse response)
            throws IOException {
        System.out.println("Doing load bill form page");
        return new VelocityContext();
    }

    public String getTemplate(){
        return "load_bill_form.vtl";
    }

}
