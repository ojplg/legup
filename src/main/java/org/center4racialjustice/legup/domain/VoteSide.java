package org.center4racialjustice.legup.domain;

import java.util.Arrays;
import java.util.List;

public final class VoteSide {

    public static final String YeaCode = "Y";
    public static final String NayCode = "N";
    public static final String NotVotingCode = "NV";
    public static final String PresentCode = "P";
    public static final String ExcusedCode = "E";
    public static final String AbsentCode = "A";

    public static final VoteSide Yea = new VoteSide(YeaCode);
    public static final VoteSide Nay = new VoteSide(NayCode);
    public static final VoteSide NotVoting = new VoteSide(NotVotingCode);
    public static final VoteSide Present = new VoteSide(PresentCode);
    public static final VoteSide Excused = new VoteSide(ExcusedCode);
    public static final VoteSide Absent = new VoteSide(AbsentCode);

    public static final List<VoteSide> AllSides =
            Arrays.asList(Yea, Nay, Present, NotVoting, Excused, Absent);

    public static final List<String> AllCodes =
            Arrays.asList(YeaCode, NayCode, PresentCode, NotVotingCode, ExcusedCode, AbsentCode);

    public static final VoteSide fromCode(String code){
        switch (code){
            case "Y" : return Yea;
            case "N" : return Nay;
            case "NV" : return NotVoting;
            case "P" : return Present;
            case "E" : return Excused;
            case "A" : return Absent;
            default : throw new RuntimeException("No such code '" + code + "'");
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

    public boolean isUncommittedVote() { return ! committedVote(); }

    public boolean committedVote(){
        return YeaCode.equals(code) || NayCode.equals(code);
    }

    public VoteSide oppositeSide(){
        switch (code) {
            case "Y" : return Nay;
            case "N" : return Yea;
            default : throw new RuntimeException("No opposite side to " + getDisplayString());
        }
    }

    public String getDisplayString(){
        switch( code ){
            case "Y" : return "Yea";
            case "N" : return "Nay";
            case "NV" : return "Not Voting";
            case "P" : return "Present";
            case "E" : return "Excused";
            case "A" : return "Absent";
            default : throw new RuntimeException("No such code " + code);
        }
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
