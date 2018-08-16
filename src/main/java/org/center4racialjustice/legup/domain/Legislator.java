package org.center4racialjustice.legup.domain;

import lombok.Data;
import org.center4racialjustice.legup.illinois.Name;

@Data
public class Legislator implements Identifiable {
    private Long id;
    private String lastName;
    private String firstName;
    private String suffix;
    private String middleInitialOrName;
    private String party;
    private Assembly assembly;
    private long district;
    private long sessionNumber;

    public Name getName(){
        return new Name(firstName, middleInitialOrName, lastName, null, suffix);
    }

    public void setAssemblyFromString(String assemblyString){
        this.assembly = Assembly.fromString(assemblyString);
    }

    public String getAssemblyString(){
        if (assembly == null){
            return null;
        }
        return assembly.toString();
    }
}
