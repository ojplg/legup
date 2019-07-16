package org.center4racialjustice.legup.domain;

import lombok.Data;

@Data
public class VoteType {

    private final String rawData;

    public boolean isThirdReading(){
        return rawData.contains("Third Reading");
    }

    public String getSummarizedType(){
        return rawData;
    }

}
