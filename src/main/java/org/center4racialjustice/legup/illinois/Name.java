package org.center4racialjustice.legup.illinois;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Name {

    public static String simpleLastNameRegex = "([A-Z][A-Za-zñ\\- ]+)";
    public static String firstInitialRegex = "([A-Z][A-Za-zñ\\-]+), ([A-Z])\\.";
    public static String fullNameRegex = "([A-Z][A-Za-zñ\\-]+), ([A-Z][A-Za-zñ\\-]+)\\s?([A-Z])?";
    public static String fullNameWithSuffixRegex = "([A-Z][A-Za-zñ\\-]+) ([A-Z][A-Za-zñ\\-])\\., ([A-Z][A-Za-zñ\\-]+)\\s?([A-Z])?";

    public static String unifiedRegex = String.join("|", simpleLastNameRegex, firstInitialRegex, fullNameRegex, fullNameWithSuffixRegex);

    public static String nameCharacters = "[A-Za-z\\., ]";

    public static Pattern simpleLastNamePattern = Pattern.compile(simpleLastNameRegex);
    public static Pattern firstInitialPattern = Pattern.compile(firstInitialRegex);
    public static Pattern fullNamePattern = Pattern.compile(fullNameRegex);
    public static Pattern fullNameWithSuffixPattern = Pattern.compile(fullNameWithSuffixRegex);

    public static Pattern unifiedPattern = Pattern.compile(unifiedRegex);

    private final String firstName;
    private final String firstInitial;
    private final String lastName;
    private final String middleInitial;
    private final String suffix;

    public static Name fromAnyString(String input){
        System.out.println(" Original string '" + input + "'");
        String trimmedInput = input.trim();
        Matcher simpleLastNameMatcher = simpleLastNamePattern.matcher(trimmedInput);
        if( simpleLastNameMatcher.matches() ){
            return new Name(null, null, trimmedInput, null, null);
        }
        Matcher firstInitialMatcher = firstInitialPattern.matcher(trimmedInput);
        if ( firstInitialMatcher.matches() ){
            String lastName = firstInitialMatcher.group(1);
            String firstInitial = firstInitialMatcher.group(2);
            return new Name(null, null, lastName, firstInitial, null);
        }
        Matcher fullNameMatcher = fullNamePattern.matcher(trimmedInput);
        if( fullNameMatcher.matches() ){
            String lastName = fullNameMatcher.group(1);
            String firstName = fullNameMatcher.group(2);
            String middleInitial = fullNameMatcher.group(3);
            return new Name(firstName, middleInitial, lastName, null, null);
        }

        Matcher fullNameWithSuffixMatcher = fullNameWithSuffixPattern.matcher(trimmedInput);
        if( fullNameWithSuffixMatcher.matches() ){
            String lastName = fullNameWithSuffixMatcher.group(1);
            String suffix = fullNameWithSuffixMatcher.group(2);
            String firstName = fullNameWithSuffixMatcher.group(3);
            String middleInitial = fullNameWithSuffixMatcher.group(4);
            return new Name(firstName, middleInitial, lastName, null, suffix);
        }

        throw new RuntimeException("Could not figure out this name: '" + trimmedInput + "'");
    }

    public static boolean isName(String input){
        try {
            fromAnyString(input);
            return true;
        } catch (RuntimeException re){
            if (re.getMessage().startsWith("Could not figure out this name")){
                return false;
            }
            throw re;
        }
    }

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

    public String toString(){
        return lastName;
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
