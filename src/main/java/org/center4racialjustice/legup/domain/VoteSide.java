package org.center4racialjustice.legup.domain;

import org.center4racialjustice.legup.db.hrorm.Converter;

public final class VoteSide {

    public static final String YeaCode = "Y";
    public static final String NayCode = "N";
    public static final String NotVotingCode = "NV";
    public static final String PresentCode = "P";

    public static final VoteSide Yea = new VoteSide(YeaCode);
    public static final VoteSide Nay = new VoteSide(NayCode);
    public static final VoteSide NotVoting = new VoteSide(NotVotingCode);
    public static final VoteSide Present = new VoteSide(PresentCode);

    public static final VoteSide fromCode(String code){
        switch (code){
            case "Y" : return Yea;
            case "N" : return Nay;
            case "NV" : return NotVoting;
            case "P" : return Present;
            case "E" : return NotVoting;
            case "A" : return NotVoting;
            default : throw new RuntimeException("No such code " + code);
        }
    }

    private final String code;

    private VoteSide(String code){
        this.code = code;
    }

    public String getCode(){
        return code;
    }

    public boolean isYes(){
        return YeaCode.equals(code);
    }

    public boolean isNo(){
        return NayCode.equals(code);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VoteSide vote = (VoteSide) o;

        return code.equals(vote.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    @Override
    public String toString() {
        return "VoteSide{" +
                "code='" + code + '\'' +
                '}';
    }
}
