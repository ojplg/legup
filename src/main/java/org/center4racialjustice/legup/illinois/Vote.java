package org.center4racialjustice.legup.illinois;

public final class Vote {

    public static final Vote Yea = new Vote("Y");
    public static final Vote Nay = new Vote("N");
    public static final Vote NotVoting = new Vote("NV");
    public static final Vote Present = new Vote("P");

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
