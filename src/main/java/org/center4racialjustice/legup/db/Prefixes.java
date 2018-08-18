package org.center4racialjustice.legup.db;

public class Prefixes {
    private static String[] letters =
            new String[] {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n"};

    private int counter = 0;

    public String nextPrefix(){
        return letters[counter++] + ".";
    }

}
