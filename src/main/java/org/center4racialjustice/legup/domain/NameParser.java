package org.center4racialjustice.legup.domain;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameParser {

    public static String president = "Mr. President";
    public static String simpleLastNameRegex = "([A-Z][A-Za-zñ\\-' ]+)";
    public static String firstInitialRegex = "([A-Z][A-Za-zñ\\-]+), ([A-Z])\\.";
    public static String fullNameRegex = "([A-Z][A-Za-zñ\\-]+), ?([A-Z][A-Za-zñ\\-]+)\\s?([A-Z])?";
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

    private final Map<String, Name> specialOverides;

    public NameParser(Map<String, Name> specialOverides) {
        this.specialOverides = specialOverides;
        // TODO: Move these overrides out of here.
        specialOverides.put("C.D. Davidsmeyer", new Name("C.D. Davidsmeyer","C.D.","","Davidsmeyer", null, null));
        specialOverides.put("La Shawn K. Ford", new Name ("La Shawn K. Ford","La Shawn", "K", "Ford", null,null));
        specialOverides.put("Andr� Thapedi", new Name("Andr� Thapedi","André","", "Thapedi", null, null));
        specialOverides.put("Wm. Sam McCann", new Name("Wm. Sam McCann", "Wm.", "Sam", "McCann",null,null));
        specialOverides.put("Antonio Mu�oz", new Name("Antonio Mu�oz", "Antonio",null,"Muñoz",null,null));
        specialOverides.put("Mr. Speaker", new Name("Mr. Speaker", "Michael", "J", "Madigan", null, null));
        specialOverides.put("Patricia Van Pelt", new Name("Patricia Van Pelt", "Patricia", null, "Van Pelt", null, null));
        specialOverides.put("Linda Chapa LaVia", new Name("Linda Chapa LaVia", "Linda", null, "Chapa LaVia", null, null));
        specialOverides.put("Mr. President", new Name("John Cullerton", "John", "J", "Cullerton", null, null));
    }

    public Name fromRegularOrderString(String input){

        String trimmedInput = input.trim();

        if (specialOverides.containsKey(trimmedInput)){
            return specialOverides.get(trimmedInput);
        }

        Matcher firstAndLastNameMatcher = firstAndLastRegularOrderPattern.matcher(trimmedInput);
        if( firstAndLastNameMatcher.matches()){
            String firstName = firstAndLastNameMatcher.group(1);
            String lastName = firstAndLastNameMatcher.group(2);
            return new Name(trimmedInput, firstName, null, lastName, null, null);
        }

        Matcher firstAndLastNameWithSuffixMatcher = firstAndLastRegularOrderWithSuffixPattern.matcher(trimmedInput);
        if( firstAndLastNameWithSuffixMatcher.matches()){
            String firstName = firstAndLastNameWithSuffixMatcher.group(1);
            String lastName = firstAndLastNameWithSuffixMatcher.group(2);
            String suffix = firstAndLastNameWithSuffixMatcher.group(3);
            return new Name(trimmedInput, firstName, null, lastName, null, suffix);
        }

        Matcher fullNameRegularOrderMatcher = fullNameRegularOrderPattern.matcher(trimmedInput);
        if( fullNameRegularOrderMatcher.matches() ){
            String firstName = fullNameRegularOrderMatcher.group(1);
            String middleInitial = fullNameRegularOrderMatcher.group(2);
            String lastName = fullNameRegularOrderMatcher.group(3);
            return new Name(trimmedInput, firstName, middleInitial, lastName, null, null);
        }

        Matcher fullNameRegularOrderWithSuffixMatcher = fullNameRegularOrderWithSuffixPattern.matcher(trimmedInput);
        if( fullNameRegularOrderWithSuffixMatcher.matches() ){
            String firstName = fullNameRegularOrderWithSuffixMatcher.group(1);
            String middleInitial = fullNameRegularOrderWithSuffixMatcher.group(2);
            String lastName = fullNameRegularOrderWithSuffixMatcher.group(3);
            String suffix = fullNameRegularOrderWithSuffixMatcher.group(4);
            return new Name(trimmedInput, firstName, middleInitial, lastName, null, suffix);
        }

        Matcher threePartNameRegularOrderMatcher = threePartNameRegularOrderPattern.matcher(trimmedInput);
        if( threePartNameRegularOrderMatcher.matches() ){
            String firstName = threePartNameRegularOrderMatcher.group(1);
            String middleInitial = threePartNameRegularOrderMatcher.group(2);
            String lastName = threePartNameRegularOrderMatcher.group(3);
            return new Name(trimmedInput, firstName, middleInitial, lastName, null, null);
        }

        throw new RuntimeException("Could not figure out this name: '" + trimmedInput + "'");
    }

    public Name fromLastNameFirstString(String input){

        String trimmedInput = input.trim();

        if (specialOverides.containsKey(trimmedInput)){
            return specialOverides.get(trimmedInput);
        }

        if( trimmedInput.equals(president)){
            return new Name(trimmedInput, null, null, president, null, null);
        }
        Matcher simpleLastNameMatcher = simpleLastNamePattern.matcher(trimmedInput);
        if( simpleLastNameMatcher.matches() ){
            return new Name(trimmedInput, null, null, trimmedInput, null, null);
        }
        Matcher firstInitialMatcher = firstInitialPattern.matcher(trimmedInput);
        if ( firstInitialMatcher.matches() ){
            String lastName = firstInitialMatcher.group(1);
            String firstInitial = firstInitialMatcher.group(2);
            return new Name(trimmedInput, null, null, lastName, firstInitial, null);
        }
        Matcher fullNameMatcher = fullNamePattern.matcher(trimmedInput);
        if( fullNameMatcher.matches() ){
            String lastName = fullNameMatcher.group(1);
            String firstName = fullNameMatcher.group(2);
            String middleInitial = fullNameMatcher.group(3);
            return new Name(trimmedInput, firstName, middleInitial, lastName, null, null);
        }
        Matcher fullNameWithSuffixMatcher = fullNameWithSuffixPattern.matcher(trimmedInput);
        if( fullNameWithSuffixMatcher.matches() ){
            String lastName = fullNameWithSuffixMatcher.group(1);
            String suffix = fullNameWithSuffixMatcher.group(2);
            String firstName = fullNameWithSuffixMatcher.group(3);
            String middleInitial = fullNameWithSuffixMatcher.group(4);
            return new Name(trimmedInput, firstName, middleInitial, lastName, null, suffix);
        }
        throw new RuntimeException("Could not figure out this name: '" + trimmedInput + "'");
    }


}
