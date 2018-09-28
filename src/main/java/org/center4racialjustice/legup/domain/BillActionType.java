package org.center4racialjustice.legup.domain;

import org.center4racialjustice.legup.db.hrorm.Converter;

import java.util.Objects;

public class BillActionType {

    public static final String VoteCode = "Vote";
    public static final String SponsorCode = "Sponsor";
    public static final String ChiefSponsorCode = "Chief Sponsor";

    public final static BillActionType VOTE = new BillActionType(VoteCode);
    public final static BillActionType SPONSOR = new BillActionType(SponsorCode);
    public final static BillActionType CHIEF_SPONSOR = new BillActionType(ChiefSponsorCode);

    public final static BillActionTypeConverter CONVERTER = new BillActionTypeConverter();

    private final String code;

    public static BillActionType fromCode(String code){
        switch (code) {
            case VoteCode : return VOTE;
            case SponsorCode : return SPONSOR;
            case ChiefSponsorCode : return CHIEF_SPONSOR;
            default : throw new RuntimeException("Unrecognized code " + code);
        }
    }

    public static int scoreValue(BillActionType billActionType){
        // These should maybe be configurable.
        switch (billActionType.getCode()) {
            case VoteCode : return 1;
            case SponsorCode : return 2;
            case ChiefSponsorCode : return 3;
            default : throw new RuntimeException("Unrecognized code " + billActionType.getCode());
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
