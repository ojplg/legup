package org.center4racialjustice.legup.web;

import org.eclipse.jetty.server.Request;

public class Util {

    public static Long getLongParameter(Request request, String parameterName){
        String parameterValueString = request.getParameter(parameterName);
        if ( parameterValueString != null && parameterValueString.length() > 0 ){
            return Long.parseLong(parameterValueString);
        }
        return null;
    }

}
