package org.center4racialjustice.legup.illinois;

import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.domain.BillEventData;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.service.PersistableAction;

import java.time.LocalDate;
import java.util.List;

@Data
public class BillVotesResults implements PersistableAction {

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
        if ( ! billEventData.getDate().equals(voteLinkInfo.getVoteDate()) ){
            // TODO: NEED SOME WIGGLE ROOM HERE
            // There are some errors on the site
            return false;
        }
        // TODO: this is maybe not right. Need to check committee dang it
        return true;
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
}
