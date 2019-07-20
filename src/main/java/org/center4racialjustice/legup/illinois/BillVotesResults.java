package org.center4racialjustice.legup.illinois;

import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.domain.BillEventData;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.service.PersistableAction;
import org.center4racialjustice.legup.util.Lists;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Data
public class BillVotesResults implements PersistableAction, VoteEventCounts {

    private static final Logger log = LogManager.getLogger(BillVotesResults.class);

    private final VoteLinkInfo voteLinkInfo;
    private final List<CollatedVote> collatedVotes;
    private final List<Name> uncollatedNames;
    private final long checksum;

    public BillVotesResults(VoteLinkInfo voteLinkInfo,
                            List<CollatedVote> collatedVotes,
                            List<Name> uncollatedNames,
                            long checksum){
        this.voteLinkInfo = voteLinkInfo;
        this.collatedVotes = collatedVotes;
        this.uncollatedNames = uncollatedNames;
        this.checksum = checksum;
    }

    public int getCollatedCount(){
        return collatedVotes.size();
    }

    public boolean matches(BillEventData billEventData){
        log.debug("Checking for match of " + billEventData + " against " + voteLinkInfo);

        if ( ! billEventData.isVote() ){
            return false;
        }
        if ( ! billEventData.getChamber().equals(voteLinkInfo.getChamber()) ){
            return false;
        }
        if ( ! closeDates(billEventData.getDate(),voteLinkInfo.getVoteDate()) ){
            return false;
        }
        if ( ! countsMatch( (VoteEventCounts) billEventData )){
            return false;
        }

        return true;
    }

    /*
     * The website sometimes puts events with dates that do not correctly
     * match the votes page. Allow one day of wiggle room.
     */
    private boolean closeDates(LocalDate dateA, LocalDate dateB){
        Period period = Period.between(dateA, dateB);
        int days = Math.abs(period.getDays());
        return days <= 1;
    }

    public LocalDate getActionDate(){
        return voteLinkInfo.getVoteDate();
    }

    @Override
    public String getDisplay() {
        StringBuilder buf = new StringBuilder();

        buf.append("Chamber ");
        buf.append(voteLinkInfo.getChamber());
        buf.append("<br//>");
        buf.append("Uncollated count ");
        buf.append(uncollatedNames.size());
        buf.append("<br//>");
        buf.append("Collated count ");
        buf.append(collatedVotes.size());
        buf.append("<br//>");
        return buf.toString();
    }

    @Override
    public int getYeaCount() {
        return Lists.countMatching(collatedVotes, v -> v.getVoteSide().isYes());
    }

    @Override
    public int getNayCount() {
        return Lists.countMatching(collatedVotes, v -> v.getVoteSide().isNo());
    }

    @Override
    public int getOtherCount() {
        return Lists.countMatching(collatedVotes, v -> v.getVoteSide().isUncommittedVote());
    }

    public boolean hasUncollatedVotes(){
        return uncollatedNames.size() > 0;
    }

    @Override
    public String toString() {
        return "BillVotesResults{" +
                "voteLinkInfo=" + voteLinkInfo +
                ", checksum=" + checksum +
                ", yeas=" + getYeaCount() +
                ", nays=" + getNayCount() +
                ", uncollatedCount=" + getUncollatedNames().stream() +
                '}';
    }
}
