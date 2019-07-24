package org.center4racialjustice.legup.domain;

import org.hrorm.Converter;

import java.util.Objects;

public class Chamber implements Comparable<Chamber> {

    public static final Chamber House = new Chamber("House");
    public static final Chamber Senate = new Chamber("Senate");

    public static final Chamber[] ALL_CHAMBERS = {
            House, Senate
    };

    public static final String[] ALL_CHAMBER_NAMES = {
        House.name,
        Senate.name
    };

    public static final Converter<Chamber, String> Converter = new Converter<Chamber, String>() {
        @Override
        public Chamber to(String s) { return Chamber.fromString(s); }

        @Override
        public String from(Chamber chamber) {
            return chamber.toString();
        }
    };

    private final String name;

    private Chamber(String name){
        this.name = name;
    }

    public static Chamber fromString(String name){
        String lowerCaseName = name.toLowerCase();
        switch(lowerCaseName){
            case "house" : return House;
            case "senate" : return Senate;
            default : throw new RuntimeException("Cannot recognize chamber named " + name);
        }
    }

    public String getName() { return name; }

    public String lowerCaseName() { return name.toLowerCase(); }

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
