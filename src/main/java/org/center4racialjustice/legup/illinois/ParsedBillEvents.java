package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.BillEvent;
import org.center4racialjustice.legup.domain.BillEventData;
import org.center4racialjustice.legup.util.Tuple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParsedBillEvents {

    private final List<BillEvent> billEventList;
    private final Map<BillEvent, BillEventData> dataMap;

    public ParsedBillEvents(List<Tuple<BillEvent, BillEventData>> tuples){
        List<BillEvent> list = new ArrayList<>();
        Map<BillEvent, BillEventData> map = new HashMap<>();

        for(Tuple<BillEvent, BillEventData> tuple : tuples){
            list.add(tuple.getFirst());
            map.put(tuple.getFirst(), tuple.getSecond());
        }

        billEventList = Collections.unmodifiableList(list);
        dataMap = Collections.unmodifiableMap(map);
    }

    public List<BillEvent> getRawEvents(){
        return billEventList;
    }

    public BillEventData getEventData(BillEvent billEvent){
        return dataMap.get(billEvent);
    }
}
