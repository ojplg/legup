package org.center4racialjustice.legup.domain;

import lombok.Data;

import java.util.List;

@Data
public class BillSaveResults {
    private final Bill bill;
    private final List<BillActionLoad> billActionLoads;
    private final List<BillAction> billActions;
}
