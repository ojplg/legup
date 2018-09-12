package org.center4racialjustice.legup.web;

import org.center4racialjustice.legup.db.hrorm.Converter;
import org.eclipse.jetty.server.Request;

public class Util {

    public static Long getLongParameter(Request request, String parameterName){
        String parameterValueString = request.getParameter(parameterName);
        if ( parameterValueString != null && parameterValueString.length() > 0 ){
            return Long.parseLong(parameterValueString);
        }
        return null;
    }

    public static <T> T getConvertedParameter(Request request, String parameterName, Converter<String, T> converter){
        String parameterValueString = request.getParameter(parameterName);
        if ( parameterValueString != null ){
            return converter.from(parameterValueString);
        }
        return null;
    }

}
