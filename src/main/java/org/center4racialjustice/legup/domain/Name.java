package org.center4racialjustice.legup.domain;

import lombok.Data;

import java.util.Objects;

@Data
public final class Name {

    private final String unparsedName;

    private final String firstName;
    private final String firstInitial;
    private final String lastName;
    private final String middle;
    private final String suffix;

    public static Name fromFirstLast(String firstName, String lastName){
        return new Name(firstName, null, lastName, null, null);
    }

    public Name(String firstName, String middle, String lastName, String firstInitial, String suffix) {
        this(null, firstName, middle, lastName, firstInitial, suffix);
    }

    public Name(String unparsedName, String firstName, String middle, String lastName, String firstInitial, String suffix){
        this.unparsedName = unparsedName;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middle = middle;
        this.firstInitial = firstInitial;
        this.suffix = suffix;
    }

    public String getDisplay(){
        StringBuilder buf = new StringBuilder();

        buf.append(lastName);
        if ( suffix != null) {
            buf.append(" ");
            buf.append(suffix);
        }
        buf.append(", ");
        buf.append(firstName);
        if ( middle != null) {
            buf.append(" ");
            buf.append(middle);
        }

        return buf.toString();
    }

    public boolean hasFirstName(){
        return firstName != null;
    }

    public boolean hasFirstInitial(){
        return firstInitial != null;
    }

    public boolean matches(Name that){
        if( this.equals(that) ){
            return true;
        }

        if( that == null || this.lastName == null ){
            return false;
        }
        if( this.hasFirstName() && that.hasFirstName()
                && ! this.firstName.equals(that.firstName)){
            return false;
        }
        if( this.hasFirstInitial() && that.hasFirstInitial()
                && ! this.firstInitial.equals(that.firstInitial)){
            return false;
        }
        if (this.hasFirstName() && that.hasFirstInitial()){
            if ( ! this.firstName.startsWith(that.firstInitial)){
                return false;
            }
        }
        if (that.hasFirstName() && this.hasFirstInitial()){
            if ( ! that.firstName.startsWith(this.firstInitial)){
                return false;
            }
        }

        return this.lastName.equals(that.lastName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Name name = (Name) o;
        return Objects.equals(firstName, name.firstName) &&
                Objects.equals(firstInitial, name.firstInitial) &&
                Objects.equals(lastName, name.lastName) &&
                Objects.equals(middle, name.middle) &&
                Objects.equals(suffix, name.suffix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, firstInitial, lastName, middle, suffix);
    }
}
