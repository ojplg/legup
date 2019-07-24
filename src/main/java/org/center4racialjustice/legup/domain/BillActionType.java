package org.center4racialjustice.legup.domain;

import org.hrorm.Converter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class BillActionType {

    public final static BillActionType VOTE = new BillActionType("Vote");
    public final static BillActionType SPONSOR = new BillActionType("Sponsor");
    public final static BillActionType CHIEF_SPONSOR = new BillActionType("Chief Sponsor");
    public final static BillActionType INTRODUCE = new BillActionType("Introduce");

    public final static BillActionType REMOVE_SPONSOR = new BillActionType("Remove Sponsor");
    public final static BillActionType REMOVE_CHIEF_SPONSOR = new BillActionType("Remove Chief Sponsor");

    public final static BillActionType COMMITTEE_REFERRAL = new BillActionType("Committee Referral");
    public final static BillActionType COMMITTEE_ASSIGNMENT = new BillActionType("Committee Assignment");
    public final static BillActionType COMMITTEE_POSTPONEMENT = new BillActionType("Committee Postponement");

    public final static BillActionType COMMITTEE_AMENDMENT_FILED = new BillActionType("Committee Amendment Filed");

    public final static BillActionType UNCLASSIFIED = new BillActionType("Unclassified");

    private static final List<BillActionType> ALL_ACTIONS = Arrays.asList(
            VOTE, SPONSOR, CHIEF_SPONSOR, INTRODUCE,
            REMOVE_SPONSOR, REMOVE_CHIEF_SPONSOR,
            COMMITTEE_REFERRAL, COMMITTEE_ASSIGNMENT,
            COMMITTEE_AMENDMENT_FILED,
            UNCLASSIFIED
    );

    public final static BillActionTypeConverter CONVERTER = new BillActionTypeConverter();

    private final String code;

    public static BillActionType fromCode(String code){
        for( BillActionType billActionType : ALL_ACTIONS ){
            if ( billActionType.getCode().equals(code)){
                return billActionType;
            }
        }
        throw new RuntimeException("Unrecognized code " + code);
    }

    public BillActionType(String code){
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BillActionType that = (BillActionType) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return "BillActionType{" +
                "code='" + code + '\'' +
                '}';
    }

    public String getCode(){
        return code;
    }

    static class BillActionTypeConverter implements Converter<BillActionType, String> {
        @Override
        public String from(BillActionType billActionType) {
            return billActionType.getCode();
        }

        @Override
        public BillActionType to(String s) {
            return BillActionType.fromCode(s);
        }
    }
}
