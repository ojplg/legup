package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.BillActionLoad;
import org.center4racialjustice.legup.util.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class BillActionLoads {

    private final List<BillActionLoad> loads;

    public BillActionLoads(List<BillActionLoad> loads){
        this.loads = Collections.unmodifiableList(new ArrayList<>(loads));
    }

    public BillActionLoad getBillHtmlLoad(){
        return findMatchingUrl( url -> url.contains("http://www.ilga.gov/legislation/BillStatus.asp"));
    }

    public BillActionLoad getHouseVotesLoad(){
        return findMatchingUrl( url ->
                url.contains("http://www.ilga.gov/legislation/votehistory")
                    && url.contains("house"));
    }

    public BillActionLoad getSenateVotesLoad(){
        return findMatchingUrl( url ->
                url.contains("http://www.ilga.gov/legislation/votehistory")
                        && url.contains("senate"));
    }

    private BillActionLoad findMatchingUrl(Predicate<String> predicate){
        return Lists.findfirst(loads, load ->  predicate.test(load.getUrl()));
    }
}
