package org.center4racialjustice.legup.illinois;

public class VoteRecord {

    private final Name name;
    private final Vote vote;

    public VoteRecord(Name name, Vote vote){
        this.name = name;
        this.vote = vote;
    }

    public Name getName() {
        return name;
    }

    public Vote getVote() {
        return vote;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VoteRecord that = (VoteRecord) o;

        if (!name.equals(that.name)) return false;
        return vote.equals(that.vote);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + vote.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "VoteRecord{" +
                "name=" + name +
                ", vote=" + vote +
                '}';
    }
}
