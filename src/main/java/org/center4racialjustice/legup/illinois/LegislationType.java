package org.center4racialjustice.legup.illinois;

import org.hrorm.Converter;

import java.util.Arrays;
import java.util.Objects;

public class LegislationType {

    public static LegislationType HOUSE_BILL = new LegislationType("House Bill", 2);
    public static LegislationType SENATE_BILL = new LegislationType("Senate Bill", 1);
    public static LegislationType SENATE_JRCA = new LegislationType("Senate Joint Resolution Constitutional Amendments", 7);

    public static LegislationType fromString(String name){
        for(LegislationType legType : Arrays.asList(HOUSE_BILL, SENATE_BILL, SENATE_JRCA)){
            if( legType.name.equals(name)){
                return legType;
            }
        }
        throw new IllegalArgumentException("Could not find legislation type for " + name);
    }

    private final String name;
    private final int htmlPageTableIndex;

    private LegislationType(String name, int htmlPageTableIndex){
        this.name = name;
        this.htmlPageTableIndex = htmlPageTableIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LegislationType that = (LegislationType) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }

    public int getHtmlPageTableIndex(){
        return htmlPageTableIndex;
    }


    public static final Converter<LegislationType, String> Converter = new Converter<LegislationType, String>() {
        @Override
        public LegislationType to(String s) { return LegislationType.fromString(s); }

        @Override
        public String from(LegislationType legislationType) {
            return legislationType.toString();
        }
    };

}
