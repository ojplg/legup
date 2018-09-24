package org.center4racialjustice.legup.domain;

import org.center4racialjustice.legup.util.Tuple;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class NameOverrides {

    private final Map<String, Name> overrides;

    public NameOverrides(Map<String, Name> overrides) {
        Map<String, Name> tmp = new HashMap<>();
        tmp.putAll(overrides);
        this.overrides = Collections.unmodifiableMap(tmp);
    }

    public static NameParser loadNameParser(String filePath){
        NameOverrides overrides = load(filePath);
        return new NameParser(overrides.getOverrides());
    }

    public static NameOverrides load(String filePath){
        try {
            Map<String, Name> overrides = new HashMap<>();

            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            reader.lines().forEach(line -> {
                        Optional<Tuple<String, Name>> item = processLine(line);
                        if (item.isPresent() ){
                            overrides.put(item.get().getFirst(), item.get().getSecond());
                        }
                    });
            return new NameOverrides(overrides);
        } catch (IOException ex){
            throw new RuntimeException(ex);
        }
    }

    public Map<String, Name> getOverrides(){
        return overrides;
    }

    private static Optional<Tuple<String, Name>> processLine(String line){
        String trimmed = line.trim();
        if ( trimmed.isEmpty() ) {
            return Optional.empty();
        }
        if ( trimmed.startsWith("#") ){
            return Optional.empty();
        }
        String[] keyValue = trimmed.split("=");
        if ( keyValue.length != 2 ){
            throw new RuntimeException("Cannot parse line: " + keyValue);
        }
        String key = keyValue[0];
        List<String> parts = splitOnPipe(keyValue[1]);
        if ( parts.size() != 5 ){
            throw new RuntimeException("Name fields have wrong number (" + parts.size() + "): " + keyValue[1]);
        }
        Name name = new Name(key, parts.get(0), parts.get(1), parts.get(2), parts.get(3), parts.get(4));
        return Optional.of(new Tuple(key, name));
    }

    private static List<String> splitOnPipe(String nameParts){
        List<String> parts = new ArrayList<>();
        StringBuilder bldr = new StringBuilder();
        for(int idx=0; idx<nameParts.length(); idx++){
            char c = nameParts.charAt(idx);
            if ( '|' == c ){
                parts.add(bldr.toString());
                bldr = new StringBuilder();
            } else {
                bldr.append(c);
            }
        }
        parts.add(bldr.toString());
        return parts;
    }
}
