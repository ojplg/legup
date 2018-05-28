package org.center4racialjustice.legup.illinois;

public final class Vote {

    public static final String YeaCode = "Y";
    public static final String NayCode = "N";
    public static final String NotVotingCode = "NV";
    public static final String PresentCode = "P";

    public static final Vote Yea = new Vote(YeaCode);
    public static final Vote Nay = new Vote(NayCode);
    public static final Vote NotVoting = new Vote(NotVotingCode);
    public static final Vote Present = new Vote(PresentCode);

    public static final Vote fromCode(String code){
        switch (code){
            case "Y" : return Yea;
            case "N" : return Nay;
            case "NV" : return NotVoting;
            case "P" : return Present;
            default : throw new RuntimeException("No such code " + code);
        }
    }

    private final String code;

    private Vote(String code){
        this.code = code;
    }

    public String getCode(){
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vote vote = (Vote) o;

        return code.equals(vote.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }
}
