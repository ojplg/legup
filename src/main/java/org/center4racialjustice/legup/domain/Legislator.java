package org.center4racialjustice.legup.domain;

import lombok.Data;

@Data
public class Legislator implements Identifiable {
    private Long id;
    private String lastName;
    private String firstName;
    private String suffix;
    private String middleInitialOrName;
    private String party;
    private Chamber chamber;
    private long district;
    private long sessionNumber;

    public Name getName(){
        return new Name(firstName, middleInitialOrName, lastName, null, suffix);
    }

    public void setAssemblyFromString(String assemblyString){
        this.chamber = Chamber.fromString(assemblyString);
    }

    public String getAssemblyString(){
        if (chamber == null){
            return null;
        }
        return chamber.toString();
    }
}
