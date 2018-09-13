package org.center4racialjustice.legup.web;

import org.center4racialjustice.legup.util.Tuple;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LegupSession {

    public static String MemberHtmlParserKey = "MemberHtmlParserKey";

    private int count = 1;
    private final Map<String, Tuple<String, Object>> storage = new HashMap<>();

    public int increment(){
        return count++;
    }

    public String setObject(String keyName, Object object){
        String oneTimeKey = UUID.randomUUID().toString();
        storage.put(keyName, new Tuple<>(oneTimeKey, object));
        return oneTimeKey;
    }

    public Object getObject(String keyName, String oneTimeKey){
        Tuple<String, Object> tuple = storage.get(keyName);
        if( tuple == null ){
            return null;
        }
        if( tuple.getFirst().equals(oneTimeKey) ){
            storage.remove(keyName);
            return tuple.getSecond();
        }
        storage.remove(keyName);
        return null;
    }

}
