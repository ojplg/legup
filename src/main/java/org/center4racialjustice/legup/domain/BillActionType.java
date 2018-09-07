package org.center4racialjustice.legup.domain;

import org.center4racialjustice.legup.db.hrorm.Converter;

import java.util.Objects;

public class BillActionType {

    public final static BillActionType VOTE = new BillActionType("Vote");
    public final static BillActionType SPONSOR = new BillActionType("Sponsor");
    public final static BillActionType CHIEF_SPONSOR = new BillActionType("Chief Sponsor");

    public final static BillActionTypeConverter CONVERTER = new BillActionTypeConverter();

    private final String code;

    public static BillActionType fromCode(String code){
        switch (code) {
            case "Vote" : return VOTE;
            case "Sponsor" : return SPONSOR;
            case "Chief Sponsor" : return CHIEF_SPONSOR;
            default : throw new RuntimeException("Unrecognized code " + code);
        }
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

    static class BillActionTypeConverter implements Converter<String, BillActionType> {
        @Override
        public String to(BillActionType billActionType) {
            return billActionType.getCode();
        }

        @Override
        public BillActionType from(String s) {
            return BillActionType.fromCode(s);
        }
    }
}
