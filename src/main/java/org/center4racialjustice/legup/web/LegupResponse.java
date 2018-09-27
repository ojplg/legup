package org.center4racialjustice.legup.web;

import org.apache.velocity.VelocityContext;

public class LegupResponse {

    // 1 template name
    // 2 use container
    // 3 content type

    private final VelocityContext velocityContext;
    private final boolean useContainer;
    private final String contentType;
    private final String templateName;
    private final String responderClassName;

    public static LegupResponse forPlaintext(Class responderClass){
        return new LegupResponse(responderClass, false, "text/plain");
    }

    public LegupResponse(Class responderClass, boolean useContainer, String contentType){
        this.velocityContext = new VelocityContext();
        this.useContainer = useContainer;
        this.contentType = contentType;
        this.responderClassName = responderClass.getSimpleName();
        this.templateName = Util.convertTitleCaseToLowercaseWithUnderlines(responderClassName) + ".vtl";
    }

    public LegupResponse(Class responderClass) {
        this.velocityContext = new VelocityContext();
        this.useContainer = true;
        this.contentType = "text/html";
        this.responderClassName = responderClass.getSimpleName();
        this.templateName = Util.convertTitleCaseToLowercaseWithUnderlines(responderClassName) + ".vtl";
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

    public String getResponderClassName(){
        return responderClassName;
    }
}
