package org.center4racialjustice.legup.domain;

import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameParser {

    public static final String[] Suffixes = {
            "III", "II", "Jr.", "Sr."
    };

    public static String simpleLastNameRegex = "([A-Z][A-Za-zéñ\\-' ]+)";
    public static String firstInitialRegex = "([A-Z][A-Za-zñ\\-' ]+), ([A-Z])\\.";
    public static String fullNameRegex = "([A-Z][A-Za-zéñ\\-' ]+), ?([A-Z][A-Za-zéñ\\-]+)\\s?([A-Z])?";
    public static String fullNameWithMiddleRegex = "([A-Z][A-Za-zéñ\\-' ]+), ([A-Z][A-Za-zéñ\\-]+) ([A-Z][A-Za-zéñ\\-]+)";

    public static String firstAndLastRegularOrder = "([A-Z][A-Za-zé\\-']+) ([A-Z][A-Za-zéñ\\-']+)";
    public static String threePartNameRegularOrder = firstAndLastRegularOrder + " ([A-Z][A-Za-z\\-']+)";
    public static String fullNameRegularOrder = "([A-Z][a-z']+) ([A-Z])\\.? ([A-Z][A-Za-z']+)";

    public static Pattern simpleLastNamePattern = Pattern.compile(simpleLastNameRegex);
    public static Pattern firstInitialPattern = Pattern.compile(firstInitialRegex);
    public static Pattern fullNamePattern = Pattern.compile(fullNameRegex);
    public static Pattern fullNameWithMiddlePattern = Pattern.compile(fullNameWithMiddleRegex);
    public static Pattern threePartNameRegularOrderPattern = Pattern.compile(threePartNameRegularOrder);

    public static Pattern fullNameRegularOrderPattern = Pattern.compile(fullNameRegularOrder);

    public static Pattern firstAndLastRegularOrderPattern = Pattern.compile(firstAndLastRegularOrder);

    private final Map<String, Name> specialOverides;

    public NameParser(){
        this.specialOverides = Collections.emptyMap();
    }

    public NameParser(Map<String, Name> specialOverides) {
        this.specialOverides = specialOverides;
    }

    public Name fromRegularOrderString(String input){

        String trimmedInput = input.trim();

        if (specialOverides.containsKey(trimmedInput)){
            return specialOverides.get(trimmedInput);
        }

        String detectedSuffix = null;
        for(String suffix : Suffixes ){
            if( trimmedInput.endsWith(", " + suffix) ){
                detectedSuffix = suffix;
                trimmedInput = trimmedInput.substring(0, trimmedInput.length() - detectedSuffix.length() - 2);
            } else if ( trimmedInput.endsWith(" " + suffix) ){
                detectedSuffix = suffix;
                trimmedInput = trimmedInput.substring(0, trimmedInput.length() - detectedSuffix.length() - 1);
            }
        }

        Matcher firstAndLastNameMatcher = firstAndLastRegularOrderPattern.matcher(trimmedInput);
        if( firstAndLastNameMatcher.matches()){
            String firstName = firstAndLastNameMatcher.group(1);
            String lastName = firstAndLastNameMatcher.group(2);
            return new Name(trimmedInput, firstName, null, lastName, null, detectedSuffix);
        }

//        Matcher firstAndLastNameWithSuffixMatcher = firstAndLastRegularOrderWithSuffixPattern.matcher(trimmedInput);
//        if( firstAndLastNameWithSuffixMatcher.matches()){
//            String firstName = firstAndLastNameWithSuffixMatcher.group(1);
//            String lastName = firstAndLastNameWithSuffixMatcher.group(2);
//            String suffix = firstAndLastNameWithSuffixMatcher.group(3);
//            return new Name(trimmedInput, firstName, null, lastName, null, suffix);
//        }

        Matcher fullNameRegularOrderMatcher = fullNameRegularOrderPattern.matcher(trimmedInput);
        if( fullNameRegularOrderMatcher.matches() ){
            String firstName = fullNameRegularOrderMatcher.group(1);
            String middleInitial = fullNameRegularOrderMatcher.group(2);
            String lastName = fullNameRegularOrderMatcher.group(3);
            return new Name(trimmedInput, firstName, middleInitial, lastName, null, detectedSuffix);
        }

//        Matcher fullNameRegularOrderWithSuffixMatcher = fullNameRegularOrderWithSuffixPattern.matcher(trimmedInput);
//        if( fullNameRegularOrderWithSuffixMatcher.matches() ){
//            String firstName = fullNameRegularOrderWithSuffixMatcher.group(1);
//            String middleInitial = fullNameRegularOrderWithSuffixMatcher.group(2);
//            String lastName = fullNameRegularOrderWithSuffixMatcher.group(3);
//            String suffix = fullNameRegularOrderWithSuffixMatcher.group(4);
//            return new Name(trimmedInput, firstName, middleInitial, lastName, null, suffix);
//        }

        Matcher threePartNameRegularOrderMatcher = threePartNameRegularOrderPattern.matcher(trimmedInput);
        if( threePartNameRegularOrderMatcher.matches() ){
            String firstName = threePartNameRegularOrderMatcher.group(1);
            String middleInitial = threePartNameRegularOrderMatcher.group(2);
            String lastName = threePartNameRegularOrderMatcher.group(3);
            return new Name(trimmedInput, firstName, middleInitial, lastName, null, detectedSuffix);
        }

        throw new RuntimeException("Could not figure out this name: '" + trimmedInput + "'");
    }

    public Name fromLastNameFirstString(String input){

        String trimmedInput = input.trim();

        if (specialOverides.containsKey(trimmedInput)){
            return specialOverides.get(trimmedInput);
        }

        String detectedSuffix = null;
        for(String suffix : Suffixes ){
            if( trimmedInput.endsWith(" " + suffix) ){
                detectedSuffix = suffix;
                trimmedInput = trimmedInput.substring(0, trimmedInput.length() - detectedSuffix.length() + 1);
            } else if( trimmedInput.contains(suffix + ", ")){
                detectedSuffix = suffix;
                int position = trimmedInput.indexOf( suffix + ", ");
                trimmedInput = trimmedInput.substring(0, position - 1) +
                        trimmedInput.substring(position + suffix.length());
            }
        }


        Matcher simpleLastNameMatcher = simpleLastNamePattern.matcher(trimmedInput);
        if( simpleLastNameMatcher.matches() ){
            return new Name(trimmedInput, null, null, trimmedInput, null, detectedSuffix);
        }
        Matcher firstInitialMatcher = firstInitialPattern.matcher(trimmedInput);
        if ( firstInitialMatcher.matches() ){
            String lastName = firstInitialMatcher.group(1);
            String firstInitial = firstInitialMatcher.group(2);
            return new Name(trimmedInput, null, null, lastName, firstInitial, detectedSuffix);
        }
        Matcher fullNameMatcher = fullNamePattern.matcher(trimmedInput);
        if( fullNameMatcher.matches() ){
            String lastName = fullNameMatcher.group(1);
            String firstName = fullNameMatcher.group(2);
            String middleInitial = fullNameMatcher.group(3);
            return new Name(trimmedInput, firstName, middleInitial, lastName, null, detectedSuffix);
        }
//        Matcher fullNameWithSuffixMatcher = fullNameWithSuffixPattern.matcher(trimmedInput);
//        if( fullNameWithSuffixMatcher.matches() ){
//            String lastName = fullNameWithSuffixMatcher.group(1);
//            String suffix = fullNameWithSuffixMatcher.group(2);
//            String firstName = fullNameWithSuffixMatcher.group(3);
//            String middleInitial = fullNameWithSuffixMatcher.group(4);
//            return new Name(trimmedInput, firstName, middleInitial, lastName, null, suffix);
//        }
        Matcher fullNameWithMiddleMatcher = fullNameWithMiddlePattern.matcher(trimmedInput);
        if( fullNameWithMiddleMatcher.matches() ){
            String lastName = fullNameWithMiddleMatcher.group(1);
            String firstName = fullNameWithMiddleMatcher.group(2);
            String middleInitial = fullNameWithMiddleMatcher.group(3);
            return new Name(trimmedInput, firstName, middleInitial, lastName, null, detectedSuffix);
        }
        throw new RuntimeException("Could not figure out this name: '" + trimmedInput + "'");
    }


}
