package org.center4racialjustice.legup.domain;

import lombok.Data;
import org.center4racialjustice.legup.illinois.BillActionLoads;

@Data
public class BillSaveResults {
    private final Bill bill;
    private final int houseVotes;
    private final int senateVotes;
    private final SponsorSaveResults sponsorSaveResults;
    private final BillActionLoads billActionLoads;
}
