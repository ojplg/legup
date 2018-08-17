package org.center4racialjustice.legup.domain;

import lombok.Data;

@Data
public class Bill implements Identifiable {

    private Long id;
    private long number;
    private Chamber chamber;

    public void setChamberFromString(String assemblyString){
        this.chamber = Chamber.fromString(assemblyString);
    }

    public String getChamberString(){
        if (chamber == null){
            return null;
        }
        return chamber.toString();
    }

}
