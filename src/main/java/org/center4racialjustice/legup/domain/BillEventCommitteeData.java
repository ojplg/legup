package org.center4racialjustice.legup.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BillEventCommitteeData {

    public static final BillEventCommitteeData EMPTY = new BillEventCommitteeData(null, null);

    private final String rawCommitteeName;
    private final String committeeId;
}
