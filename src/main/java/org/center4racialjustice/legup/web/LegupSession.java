package org.center4racialjustice.legup.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.domain.Organization;
import org.center4racialjustice.legup.domain.User;
import org.center4racialjustice.legup.util.Tuple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LegupSession {

    private static final Logger log = LogManager.getLogger(LegupSession.class);

    public static String UnknownLegislatorsKey = "UnknownLegislatorsKey";
//    public static String BillSearchResultsKey = "BillSearchResultsKey";
    public static String ReportCardGradesKey = "ReportCardGradesKey";
    public static String CommitteeDataKey = "CommiteeDataKey";
    public static String BillStatusComputerKey = "BillStatusComputerKey";

    private int count = 1;
    private final Map<String, Tuple<String, Object>> storage = new HashMap<>();
    private User loggedInUser;
    private List<Organization> organizations;

    public int increment(){
        return count++;
    }

    public String setObject(String keyName, Object object){
        String oneTimeKey = UUID.randomUUID().toString();
        storage.put(keyName, new Tuple<>(oneTimeKey, object));
        return oneTimeKey;
    }

    // Perhaps this should return a tuple with a message
    // describing the error
    public Object getObject(String keyName, String oneTimeKey){
        Tuple<String, Object> tuple = storage.get(keyName);
        if( tuple == null ){
            log.warn("No tuple for key " + keyName + " with one time key " + oneTimeKey);
            return null;
        }
        if( tuple.getFirst().equals(oneTimeKey) ){
            //storage.remove(keyName);
            return tuple.getSecond();
        }
        //storage.remove(keyName);
        log.warn("Bad one time key for " + keyName + ". Expected: " + tuple.getFirst() + " but provided " + oneTimeKey);
        return null;
    }

    public void setLoggedInUser(User user, List<Organization> organizations){
        this.loggedInUser = user;
        this.organizations = organizations;
    }

    public User getLoggedInUser(){
        return this.loggedInUser;
    }

    public boolean isLoggedInSuperUser() {
        return this.loggedInUser != null ? loggedInUser.isSuperUser() : false;
    }

    public void logout(){
        this.loggedInUser = null;
    }

    public List<Organization> getLoggedInUsersOrganizations(){
        return organizations;
    }
}
