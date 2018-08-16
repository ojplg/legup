package org.center4racialjustice.legup.domain;

public class Assembly {

    public static final Assembly House = new Assembly("House");
    public static final Assembly Senate = new Assembly("Senate");

    private final String name;

    private Assembly(String name){
        this.name = name;
    }

    public static Assembly fromString(String name){
        switch(name){
            case "House" : return House;
            case "Senate" : return Senate;
            default : throw new RuntimeException("Cannot recognize assembly named " + name);
        }
    }

    public String toString(){
        return name;
    }
}
