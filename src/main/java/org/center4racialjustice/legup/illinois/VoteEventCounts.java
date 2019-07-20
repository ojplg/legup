package org.center4racialjustice.legup.illinois;

public interface VoteEventCounts {

    int getYeaCount();
    int getNayCount();
    int getOtherCount();

    default boolean countsMatch(VoteEventCounts other){
        return getYeaCount() == other.getYeaCount()
                && getNayCount() == other.getNayCount()
                && getOtherCount() == other.getOtherCount(); }
}
