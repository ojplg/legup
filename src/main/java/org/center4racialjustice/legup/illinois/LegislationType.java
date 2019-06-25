package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Chamber;
import org.hrorm.Converter;

import java.util.Objects;

public class LegislationType {

    public static final String BILL_SUB_TYPE = "Bill";
    public static final String RESOLUTION_SUB_TYPE = "Resolution";
    public static final String JOINT_RESOLUTION_SUB_TYPE = "Joint Resolution";
    public static final String JRCA_SUB_TYPE = "Joint Resolution Constitutional Amendment";

    public static final LegislationType SENATE_BILL = new LegislationType(Chamber.Senate,BILL_SUB_TYPE, 1);
    public static final LegislationType HOUSE_BILL = new LegislationType(Chamber.House,BILL_SUB_TYPE, 2);
    public static final LegislationType SENATE_RESOLUTION = new LegislationType(Chamber.Senate,RESOLUTION_SUB_TYPE, 3);
    public static final LegislationType HOUSE_RESOLUTION = new LegislationType(Chamber.House,RESOLUTION_SUB_TYPE, 4);
    public static final LegislationType SENATE_JOINT_RESOLUTION = new LegislationType(Chamber.Senate,JOINT_RESOLUTION_SUB_TYPE, 5);
    public static final LegislationType HOUSE_JOINT_RESOLUTION = new LegislationType(Chamber.House,JOINT_RESOLUTION_SUB_TYPE, 6);
    public static final LegislationType SENATE_JRCA = new LegislationType(Chamber.Senate,JRCA_SUB_TYPE, 7);
    public static final LegislationType HOUSE_JRCA = new LegislationType(Chamber.House,JRCA_SUB_TYPE, 8);

    public static final LegislationType[] ALL_TYPES = {
            SENATE_BILL,
            HOUSE_BILL,
            SENATE_RESOLUTION,
            HOUSE_RESOLUTION,
            SENATE_JOINT_RESOLUTION,
            HOUSE_JOINT_RESOLUTION,
            SENATE_JRCA,
            HOUSE_JRCA
    };

    public static final String[] ALL_SUB_TYPES = {
        BILL_SUB_TYPE,
        RESOLUTION_SUB_TYPE,
        JOINT_RESOLUTION_SUB_TYPE,
        JRCA_SUB_TYPE
    };

    public static String subTypeStringFromCode(String code){
        switch( code ){
            case "B" : return BILL_SUB_TYPE;
            case "R" : return RESOLUTION_SUB_TYPE;
            case "JR" : return JOINT_RESOLUTION_SUB_TYPE;
            case "JRCA" : return JRCA_SUB_TYPE;
            default : throw new IllegalArgumentException("Cannot recognize: " + code);
        }
    }

    public static LegislationType fromString(String name){
        for(LegislationType legType : ALL_TYPES){
            if( legType.getName().equals(name)){
                return legType;
            }
        }
        throw new IllegalArgumentException("Could not find legislation type for " + name);
    }

    public static LegislationType fromChamberAndSubType(Chamber chamber, String subType){
        String name = formName(chamber, subType);
        return fromString(name);
    }

    private final Chamber chamber;
    private final String subType;
    private final int htmlPageTableIndex;

    private LegislationType(Chamber chamber, String subType, int htmlPageTableIndex){
        this.chamber = chamber;
        this.subType = subType;
        this.htmlPageTableIndex = htmlPageTableIndex;
    }

    public String getName(){
        return formName(chamber, subType);
    }

    private static String formName(Chamber chamber, String subType){
        return chamber.getName() + " " + subType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LegislationType that = (LegislationType) o;
        return htmlPageTableIndex == that.htmlPageTableIndex &&
                Objects.equals(chamber, that.chamber) &&
                Objects.equals(subType, that.subType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chamber, subType, htmlPageTableIndex);
    }

    @Override
    public String toString() {
        return getName();
    }

    public int getHtmlPageTableIndex(){
        return htmlPageTableIndex;
    }

    public Chamber getChamber(){
        return chamber;
    }

    public String getSubType(){
        return subType;
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
