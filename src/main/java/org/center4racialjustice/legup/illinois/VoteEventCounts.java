package org.center4racialjustice.legup.illinois;

public interface VoteEventCounts {

    int getYeaCount();
    int getNayCount();
    int getOtherCount();

    default boolean countsMatch(VoteEventCounts other){
        // NOTE: innaccurate web site requires some slush
        int otherCountDiff = Math.abs(getOtherCount() - other.getOtherCount());
        return getYeaCount() == other.getYeaCount()
                && getNayCount() == other.getNayCount(); }
}
