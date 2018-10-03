package org.center4racialjustice.legup.web;

import org.apache.velocity.VelocityContext;

public class LegupResponse {

    private final VelocityContext velocityContext;
    private final boolean useContainer;
    private final String contentType;
    private final String templateName;

    public static LegupResponse forPlaintext(Class responderClass){
        return new LegupResponse(responderClass, false, "text/plain");
    }

    public static LegupResponse forError(Class formClass, String errorMessage){
        LegupResponse legupResponse = new LegupResponse(formClass);
        legupResponse.putVelocityData("errorMessage", errorMessage);
        return legupResponse;
    }

    public LegupResponse(Class responderClass, boolean useContainer, String contentType){
        this.velocityContext = new VelocityContext();
        this.useContainer = useContainer;
        this.contentType = contentType;
        this.templateName = Util.classNameToLowercaseWithUnderlines(responderClass) + ".vtl";
    }

    public LegupResponse(Class responderClass) {
        this(responderClass, true, "text/html");
    }

    public void putVelocityData(String key, Object value){
        velocityContext.put(key, value);
    }

    public VelocityContext getVelocityContext() {
        return velocityContext;
    }

    public boolean useContainer(){
        return useContainer;
    }

    public String getContentType(){
        return contentType;
    }

    public String getTemplateName(){
        return templateName;
    }
}
