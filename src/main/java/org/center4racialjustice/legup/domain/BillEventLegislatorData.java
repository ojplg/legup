package org.center4racialjustice.legup.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BillEventLegislatorData {

    public static final BillEventLegislatorData EMPTY = new BillEventLegislatorData(null, null, null);

    private final String rawLegislatorName;
    private final Name parsedLegislatorName;
    private final String legislatorMemberId;

}
