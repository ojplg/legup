package org.center4racialjustice.legup.domain;

import java.util.Objects;

public class Chamber implements Comparable<Chamber> {

    public static final Chamber House = new Chamber("House");
    public static final Chamber Senate = new Chamber("Senate");

    private final String name;

    private Chamber(String name){
        this.name = name;
    }

    public static Chamber fromString(String name){
        switch(name){
            case "House" : return House;
            case "Senate" : return Senate;
            case "HOUSE" : return House;
            case "SENATE" : return Senate;
            default : throw new RuntimeException("Cannot recognize chamber named " + name);
        }
    }

    public String getName() { return name; }

    public String toString(){
        return name;
    }

    @Override
    public int compareTo(Chamber o) {
        return this.name.compareTo(o.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chamber chamber = (Chamber) o;
        return Objects.equals(name, chamber.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
