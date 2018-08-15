package org.center4racialjustice.legup.illinois;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Name {

    public static String president = "Mr. President";
    public static String simpleLastNameRegex = "([A-Z][A-Za-zñ\\- ]+)";
    public static String firstInitialRegex = "([A-Z][A-Za-zñ\\-]+), ([A-Z])\\.";
    public static String fullNameRegex = "([A-Z][A-Za-zñ\\-]+), ([A-Z][A-Za-zñ\\-]+)\\s?([A-Z])?";
    public static String fullNameWithSuffixRegex = "([A-Z][A-Za-zñ\\-]+) ([A-Z][A-Za-zñ\\-])\\., ([A-Z][A-Za-zñ\\-]+)\\s?([A-Z])?";

    public static String firstAndLastRegularOrder = "([A-Z][A-Za-z\\-']+) ([A-Z][A-Za-zñ\\-']+)";
    public static String threePartNameRegularOrder = firstAndLastRegularOrder + " ([A-Z][A-Za-z\\-']+)";
    public static String firstAndLastRegularOrderWithSuffix = firstAndLastRegularOrder + ", ([A-Z][A-Za-z]+)\\.?";
    public static String fullNameRegularOrder = "([A-Z][a-z']+) ([A-Z]).? ([A-Z][A-Za-z']+)";
    public static String fullNameRegularOrderWithSuffix = fullNameRegularOrder + ", ([A-Z][A-Za-zñ]+).";

    public static String unifiedRegex = String.join("|", simpleLastNameRegex, firstInitialRegex, fullNameRegex, fullNameWithSuffixRegex);

    public static Pattern simpleLastNamePattern = Pattern.compile(simpleLastNameRegex);
    public static Pattern firstInitialPattern = Pattern.compile(firstInitialRegex);
    public static Pattern fullNamePattern = Pattern.compile(fullNameRegex);
    public static Pattern fullNameWithSuffixPattern = Pattern.compile(fullNameWithSuffixRegex);
    public static Pattern threePartNameRegularOrderPattern = Pattern.compile(threePartNameRegularOrder);

    public static Pattern fullNameRegularOrderPattern = Pattern.compile(fullNameRegularOrder);
    public static Pattern fullNameRegularOrderWithSuffixPattern = Pattern.compile(fullNameRegularOrderWithSuffix);

    public static Pattern firstAndLastRegularOrderPattern = Pattern.compile(firstAndLastRegularOrder);
    public static Pattern firstAndLastRegularOrderWithSuffixPattern = Pattern.compile(firstAndLastRegularOrderWithSuffix);

    private static Map<String, Name> specialOverides = new HashMap<>();

    private final String firstName;
    private final String firstInitial;
    private final String lastName;
    private final String middleInitial;
    private final String suffix;

    static {
        specialOverides.put("C.D. Davidsmeyer", new Name("C.D.","","Davidsmeyer", null, null));
        specialOverides.put("La Shawn K. Ford", new Name ("La Shawn", "K", "Ford", null,null));
        specialOverides.put("Andr� Thapedi", new Name("André","", "Thapedi", null, null));
        specialOverides.put("Wm. Sam McCann", new Name("Wm.", "Sam", "McCann",null,null));
        specialOverides.put("Antonio Mu�oz", new Name("Antonio",null,"Muñoz",null,null));
    }

    public static Name fromRegularOrderString(String input){

        String trimmedInput = input.trim();

        if (specialOverides.containsKey(trimmedInput)){
            return specialOverides.get(trimmedInput);
        }

        Matcher firstAndLastNameMatcher = firstAndLastRegularOrderPattern.matcher(trimmedInput);
        if( firstAndLastNameMatcher.matches()){
            String firstName = firstAndLastNameMatcher.group(1);
            String lastName = firstAndLastNameMatcher.group(2);
            return new Name(firstName, null, lastName, null, null);
        }

        Matcher firstAndLastNameWithSuffixMatcher = firstAndLastRegularOrderWithSuffixPattern.matcher(trimmedInput);
        if( firstAndLastNameWithSuffixMatcher.matches()){
            String firstName = firstAndLastNameWithSuffixMatcher.group(1);
            String lastName = firstAndLastNameWithSuffixMatcher.group(2);
            String suffix = firstAndLastNameWithSuffixMatcher.group(3);
            return new Name(firstName, null, lastName, null, suffix);
        }

        Matcher fullNameRegularOrderMatcher = fullNameRegularOrderPattern.matcher(trimmedInput);
        if( fullNameRegularOrderMatcher.matches() ){
            String firstName = fullNameRegularOrderMatcher.group(1);
            String middleInitial = fullNameRegularOrderMatcher.group(2);
            String lastName = fullNameRegularOrderMatcher.group(3);
            return new Name(firstName, middleInitial, lastName, null, null);
        }

        Matcher fullNameRegularOrderWithSuffixMatcher = fullNameRegularOrderWithSuffixPattern.matcher(trimmedInput);
        if( fullNameRegularOrderWithSuffixMatcher.matches() ){
            String firstName = fullNameRegularOrderWithSuffixMatcher.group(1);
            String middleInitial = fullNameRegularOrderWithSuffixMatcher.group(2);
            String lastName = fullNameRegularOrderWithSuffixMatcher.group(3);
            String suffix = fullNameRegularOrderWithSuffixMatcher.group(4);
            return new Name(firstName, middleInitial, lastName, null, suffix);
        }

        Matcher threePartNameRegularOrderMatcher = threePartNameRegularOrderPattern.matcher(trimmedInput);
        if( threePartNameRegularOrderMatcher.matches() ){
            String firstName = threePartNameRegularOrderMatcher.group(1);
            String middleInitial = threePartNameRegularOrderMatcher.group(2);
            String lastName = threePartNameRegularOrderMatcher.group(3);
            return new Name(firstName, middleInitial, lastName, null, null);
        }

        throw new RuntimeException("Could not figure out this name: '" + trimmedInput + "'");
    }

    public static Name fromLastNameFirstString(String input){
        String trimmedInput = input.trim();
        if( trimmedInput.equals(president)){
            return new Name(null, null, president, null, null);
        }
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
            fromLastNameFirstString(input);
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
