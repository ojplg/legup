package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.BillActionLoad;
import org.center4racialjustice.legup.util.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BillActionLoads {

    private final List<BillActionLoad> loads;

    public BillActionLoads(List<BillActionLoad> loads){
        this.loads = Collections.unmodifiableList(new ArrayList<>(loads));
    }

    public BillActionLoads(BillActionLoad ... billActionLoads){
        this.loads = Collections.unmodifiableList(Arrays.asList(billActionLoads));
    }

    public BillActionLoad getBillHtmlLoad(){
        return Lists.findfirst(loads, BillActionLoad::isBillLoad);
    }

    public List<String> getVoteLoadKeys(){
        return Lists.map(loads, BillActionLoad::getKey);
    }

    public BillActionLoad getByKey(String key){
        return Lists.findfirst(loads, load -> load.getKey().equals(key));
    }

    public List<BillActionLoad> getVoteLoads(){
        return Lists.filter(loads, BillActionLoad::isVoteLoad);
    }

}
