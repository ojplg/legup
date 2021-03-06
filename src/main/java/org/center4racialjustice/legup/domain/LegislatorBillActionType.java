package org.center4racialjustice.legup.domain;

import org.hrorm.Converter;

import java.util.Objects;

// FIXME: this class should not exist
// and if it has to, the thing should work better
public class LegislatorBillActionType {

    public static final String VoteCode = "Vote";
    public static final String SponsorCode = "Sponsor";
    public static final String ChiefSponsorCode = "Chief Sponsor";
    public static final String IntroduceCode = "Introduce";

    public static final String RemoveSponsorCode = "Remove Sponsor";
    public static final String RemoveChiefSponsorCode = "Remove Chief Sponsor";


    public final static LegislatorBillActionType VOTE = new LegislatorBillActionType(VoteCode, 1);
    public final static LegislatorBillActionType SPONSOR = new LegislatorBillActionType(SponsorCode, 2);
    public final static LegislatorBillActionType CHIEF_SPONSOR = new LegislatorBillActionType(ChiefSponsorCode, 3);
    public final static LegislatorBillActionType INTRODUCE = new LegislatorBillActionType(IntroduceCode, 4);

    public final static LegislatorBillActionType REMOVE_SPONSOR = new LegislatorBillActionType(RemoveSponsorCode, -2);
    public final static LegislatorBillActionType REMOVE_CHIEF_SPONSOR = new LegislatorBillActionType(RemoveChiefSponsorCode, -3);


    public final static LegislatorBillActionTypeConverter CONVERTER = new LegislatorBillActionTypeConverter();

    private final String code;
    private final int scoreValue;

    public static LegislatorBillActionType fromBillActionType(BillActionType billActionType){
        return fromCode(billActionType.getCode());
    }

    public static LegislatorBillActionType fromCode(String code){
        switch (code) {
            case VoteCode : return VOTE;
            case SponsorCode : return SPONSOR;
            case ChiefSponsorCode : return CHIEF_SPONSOR;
            case IntroduceCode : return INTRODUCE;
            case RemoveChiefSponsorCode: return REMOVE_CHIEF_SPONSOR;
            case RemoveSponsorCode : return REMOVE_SPONSOR;
            default : throw new RuntimeException("Unrecognized code " + code);
        }
    }

    public int scoreValue(){
        return scoreValue;
    }

    public LegislatorBillActionType(String code, int scoreValue){
        this.code = code;
        this.scoreValue = scoreValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LegislatorBillActionType that = (LegislatorBillActionType) o;
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

    static class LegislatorBillActionTypeConverter implements Converter<LegislatorBillActionType, String> {
        @Override
        public String from(LegislatorBillActionType billActionType) {
            return billActionType.getCode();
        }

        @Override
        public LegislatorBillActionType to(String s) {
            return LegislatorBillActionType.fromCode(s);
        }
    }
}
