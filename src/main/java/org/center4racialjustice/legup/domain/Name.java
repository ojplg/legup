package org.center4racialjustice.legup.domain;

import lombok.Data;

@Data
public final class Name {

    private final String unparsedName;

    private final String firstName;
    private final String firstInitial;
    private final String lastName;
    private final String middleInitial;
    private final String suffix;

    public static Name fromFirstLastMiddleInitial(String firstName, String lastName, String middleInitial){
        return new Name(firstName, middleInitial, lastName, null, null);
    }

    public static Name fromFirstLast(String firstName, String lastName){
        return new Name(firstName, null, lastName, null, null);
    }

    public Name(String firstName, String middleInitial, String lastName, String firstInitial, String suffix) {
        this(null, firstName, middleInitial, lastName, firstInitial, suffix);
    }

    public Name(String unparsedName, String firstName, String middleInitial, String lastName, String firstInitial, String suffix){
        this.unparsedName = unparsedName;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleInitial = middleInitial;
        this.firstInitial = firstInitial;
        this.suffix = suffix;
    }

    public boolean matches(Name name){
        if( name == null || this.lastName == null ){
            return false;
        }
        return this.lastName.equals(name.lastName);
    }

}
