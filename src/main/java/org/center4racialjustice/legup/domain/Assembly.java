package org.center4racialjustice.legup.domain;

public class Assembly {

    public static final Assembly House = new Assembly("House");
    public static final Assembly Senate = new Assembly("Senate");

    private final String name;

    private Assembly(String name){
        this.name = name;
    }

}
