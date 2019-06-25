package org.center4racialjustice.legup.domain;

import lombok.Data;
import org.center4racialjustice.legup.illinois.LegislationType;

@Data
public class LegislationIdentity {
    private LegislationType legislationType;
    private Long number;

    public Chamber getChamber() {
        return legislationType.getChamber();
    }

    public String getLegislationSubType(){
        return legislationType.getSubType();
    }
}
