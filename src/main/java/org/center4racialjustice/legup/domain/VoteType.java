package org.center4racialjustice.legup.domain;

public class VoteType {

    private final String rawData;

    public VoteType(String rawData) {
        this.rawData = rawData;
    }

    public boolean isThirdReading(){
        return rawData.contains("Third Reading");
    }

    public String getSummarizedType(){
        return rawData;
    }

    public String getRawData() {
        return rawData;
    }
}
