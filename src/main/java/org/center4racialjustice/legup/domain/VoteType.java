package org.center4racialjustice.legup.domain;

import lombok.Data;

@Data
public class VoteType {

    private final String description;

    public boolean isThirdReading(){
        return description.contains("Third Reading");
    }

    public String getSummarizedType(){
        return description;
    }

}
