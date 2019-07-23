package org.center4racialjustice.legup.illinois;

import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.domain.BillAction;
import org.center4racialjustice.legup.domain.BillActionLoad;
import org.center4racialjustice.legup.domain.BillActionType;
import org.center4racialjustice.legup.domain.BillEvent;
import org.center4racialjustice.legup.domain.BillEventLegislatorData;
import org.center4racialjustice.legup.domain.LegislatorBillAction;
import org.center4racialjustice.legup.domain.LegislatorBillActionType;
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

    public boolean matches(BillEvent billEventData){
        log.debug("Checking for match of " + billEventData + " against " + voteLinkInfo);

        if ( ! billEventData.getBillActionType().equals(BillActionType.VOTE) ){
            return false;
        }
        if ( ! billEventData.getChamber().equals(voteLinkInfo.getChamber()) ){
            return false;
        }
        if ( ! closeDates(billEventData.getDate(),voteLinkInfo.getVoteDate()) ){
            return false;
        }
        VoteEventCountExtractor voteEventCountExtractor = new VoteEventCountExtractor(billEventData.getRawContents());
        if ( ! countsMatch( voteEventCountExtractor )){
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

        buf.append(voteLinkInfo.getChamber());
        buf.append("<br//>");
        buf.append("Uncollated count: ");
        buf.append(uncollatedNames.size());
        buf.append("<br//>");
        buf.append("Collated count: ");
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

    @Override
    public List<String> getErrors() {
        return Lists.map(uncollatedNames, name -> "Uncollated: " + name.getDisplay());
    }

    public List<LegislatorBillAction> asLegislatorActions(){
        if( hasError()){
            throw new RuntimeException("Cannot persist with errors " + getErrors());
        }
        return Lists.map(collatedVotes, CollatedVote::asLegislatorBillAction);
    }

    @Override
    public BillAction asBillAction(BillActionLoad persistedLoad) {
        throw new UnsupportedOperationException();
    }
}
