package org.center4racialjustice.legup.web;

import org.apache.velocity.VelocityContext;
import org.center4racialjustice.legup.domain.User;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HtmlLegupResponse implements LegupResponse {

    private final VelocityContext velocityContext;
    private final String templateName;

    public static LegupResponse forError(Class formClass, String errorMessage, Map<String, String> errors){
        HtmlLegupResponse legupResponse = new HtmlLegupResponse(formClass);
        legupResponse.putVelocityData("errorMessage", errorMessage);
        for(Map.Entry<String, String> entry : errors.entrySet()){
            legupResponse.putVelocityData(entry.getKey() + "Error", entry.getValue());
        }
        return legupResponse;
    }

    public static HtmlLegupResponse withLinks(Class responderClass, List<NavLink> links){
        return withLinks(responderClass, null, links);
    }

    public static HtmlLegupResponse withLinks(Class responderClass, User user, List<NavLink> links){
        HtmlLegupResponse response = new HtmlLegupResponse(responderClass);

        TopMatter topMatter = new TopMatter(user, links, "");
        response.putVelocityData("topmatter", topMatter);

        return response;
    }

    public static HtmlLegupResponse withHelpAndLinks(Class responderClass, User user, List<NavLink> links){
        HtmlLegupResponse response = new HtmlLegupResponse(responderClass);

        TopMatter topMatter = new TopMatter(user, links, "/legup/help/" + Util.classNameToLowercaseWithUnderlines(responderClass));
        response.putVelocityData("topmatter", topMatter);

        return response;
    }

    public static HtmlLegupResponse withHelpAndLinks(Class responderClass, List<NavLink> links){
        return withHelpAndLinks(responderClass, null, links);
    }

    public static HtmlLegupResponse withHelp(Class responderClass){
        return withHelpAndLinks(responderClass, null, Collections.emptyList());
    }

    public HtmlLegupResponse(Class responderClass){
        this.velocityContext = new VelocityContext();
        this.templateName = Util.classNameToLowercaseWithUnderlines(responderClass) + ".vtl";
    }

    public void putVelocityData(String key, Object value){
        velocityContext.put(key, value);
    }

    @Override
    public VelocityContext getVelocityContext() {
        return velocityContext;
    }

    @Override
    public boolean useContainer(){
        return true;
    }

    @Override
    public String getContentType(){
        return "text/html";
    }

    @Override
    public String actionKey(){
        return templateName;
    }

    @Override
    public boolean shouldRender() {
        return true;
    }

    @Override
    public Map<String, String> getParameters() {
        throw new UnsupportedOperationException();
    }
}
