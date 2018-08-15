package org.center4racialjustice.legup.domain;

import lombok.Data;
import org.center4racialjustice.legup.illinois.Name;

@Data
public class Legislator {
    private final Name name;
    private final String party;
    private final Assembly assembly;
    private final int district;
    private final int session;
}
