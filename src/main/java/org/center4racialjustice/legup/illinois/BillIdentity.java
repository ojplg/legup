package org.center4racialjustice.legup.illinois;

import lombok.Data;
import org.center4racialjustice.legup.domain.Chamber;

@Data
public class BillIdentity {

    private final long session;
    private final Chamber chamber;
    private final LegislationType legislationType;
    private final long number;

    public String getSubType(){
        return legislationType.getSubType();
    }
}
