package org.center4racialjustice.legup.domain;

import lombok.Data;
import org.center4racialjustice.legup.illinois.BillActionLoads;

import java.util.List;

@Data
public class BillSaveResults {
    private final Bill bill;
    private final BillActionLoads billActionLoads;
    private final List<BillAction> billActions;
}
