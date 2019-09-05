package org.center4racialjustice.legup.illinois;

public class BadHtmlCleaner {

    private static String[] BAD_PREFIXES = {
            "&bsp? ",
            "&p; ",
            "&p;"
    };

    public static String cleanDateString(String dirtyDate){
        for(String prefix : BAD_PREFIXES){
            if( dirtyDate.startsWith(prefix)){
                return dirtyDate.substring(prefix.length());
            }
        }
        return dirtyDate;
    }
}
