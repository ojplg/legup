package org.center4racialjustice.legup.illinois;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Name {

    public static String simpleLastNameRegex = "([A-Z][a-z]+)";
    public static String firstInitialRegex = "([A-Z][a-z]+), ([A-Z])\\.";
    public static String fullNameRegex = "([A-Z][a-z]+), ([A-Z][a-z]+)\\s?([A-Z])?";
    public static String fullNameWithSuffixRegex = "([A-Z][a-z]+) ([A-Z][a-z])\\., ([A-Z][a-z]+)\\s?([A-Z])?";

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
        Matcher simpleLastNameMatcher = simpleLastNamePattern.matcher(input);
        if( simpleLastNameMatcher.matches() ){
            return new Name(null, null, input, null, null);
        }
        Matcher firstInitialMatcher = firstInitialPattern.matcher(input);
        if ( firstInitialMatcher.matches() ){
            String lastName = firstInitialMatcher.group(1);
            String firstInitial = firstInitialMatcher.group(2);
            return new Name(null, null, lastName, firstInitial, null);
        }
        Matcher fullNameMatcher = fullNamePattern.matcher(input);
        if( fullNameMatcher.matches() ){
            String lastName = fullNameMatcher.group(1);
            String firstName = fullNameMatcher.group(2);
            String middleInitial = fullNameMatcher.group(3);
            return new Name(firstName, middleInitial, lastName, null, null);
        }

        Matcher fullNameWithSuffixMatcher = fullNameWithSuffixPattern.matcher(input);
        if( fullNameWithSuffixMatcher.matches() ){
            String lastName = fullNameWithSuffixMatcher.group(1);
            String suffix = fullNameWithSuffixMatcher.group(2);
            String firstName = fullNameWithSuffixMatcher.group(3);
            String middleInitial = fullNameWithSuffixMatcher.group(4);
            return new Name(firstName, middleInitial, lastName, null, suffix);
        }

        throw new RuntimeException("Could not figure out this name: '" + input + "'");
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

}
