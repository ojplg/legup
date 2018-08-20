package org.center4racialjustice.legup.domain;

public final class Name {

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

    public Name(String firstName, String middleInitial, String lastName, String firstInitial, String suffix){
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleInitial = middleInitial;
        this.firstInitial = firstInitial;
        this.suffix = suffix;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getFirstInitial() {
        return firstInitial;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMiddleInitial() {
        return middleInitial;
    }

    public String getSuffix() {
        return suffix;
    }

    public boolean matches(Name name){
        if( name == null || this.lastName == null ){
            return false;
        }
        return this.lastName.equals(name.lastName);
    }

    @Override
    public String toString() {
        return "Name{" +
                "firstName='" + firstName + '\'' +
                ", firstInitial='" + firstInitial + '\'' +
                ", lastName='" + lastName + '\'' +
                ", middleInitial='" + middleInitial + '\'' +
                ", suffix='" + suffix + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Name name = (Name) o;

        if (firstName != null ? !firstName.equals(name.firstName) : name.firstName != null) return false;
        if (firstInitial != null ? !firstInitial.equals(name.firstInitial) : name.firstInitial != null) return false;
        if (lastName != null ? !lastName.equals(name.lastName) : name.lastName != null) return false;
        if (middleInitial != null ? !middleInitial.equals(name.middleInitial) : name.middleInitial != null)
            return false;
        return suffix != null ? suffix.equals(name.suffix) : name.suffix == null;
    }

    @Override
    public int hashCode() {
        int result = firstName != null ? firstName.hashCode() : 0;
        result = 31 * result + (firstInitial != null ? firstInitial.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (middleInitial != null ? middleInitial.hashCode() : 0);
        result = 31 * result + (suffix != null ? suffix.hashCode() : 0);
        return result;
    }
}
