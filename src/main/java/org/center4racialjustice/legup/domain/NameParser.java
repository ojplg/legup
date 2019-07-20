package org.center4racialjustice.legup.domain;

import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameParser {

    public static final String[] Suffixes = {
            "III", "II", "Jr.", "Sr."
    };

    private static String SpacelessNameCaptureRegex = "([A-Z][A-Za-zéñ\\-']+)";
    private static String SpaceAllowedNameCaptureRegex = "([A-Z][A-Za-zéñ\\-' ]+)";

    public static String simpleLastNameRegex = SpaceAllowedNameCaptureRegex;
    public static String firstInitialRegex = SpaceAllowedNameCaptureRegex + ", ([A-Z])\\.";
    public static String fullNameRegex = SpaceAllowedNameCaptureRegex + ", ?" + SpacelessNameCaptureRegex + "\\s?([A-Z])?";
    public static String fullNameWithMiddleRegex = SpaceAllowedNameCaptureRegex + ", " +
            SpacelessNameCaptureRegex + " " + SpacelessNameCaptureRegex;

    public static String firstAndLastRegularOrder = SpacelessNameCaptureRegex + " " + SpaceAllowedNameCaptureRegex;
    public static String threePartNameRegularOrder = SpacelessNameCaptureRegex + " " + SpacelessNameCaptureRegex
            + " " + SpaceAllowedNameCaptureRegex;
    public static String fullNameRegularOrder = SpacelessNameCaptureRegex + " ([A-Z])\\.? " + SpaceAllowedNameCaptureRegex;

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

        Matcher fullNameRegularOrderMatcher = fullNameRegularOrderPattern.matcher(trimmedInput);
        if( fullNameRegularOrderMatcher.matches() ){
            String firstName = fullNameRegularOrderMatcher.group(1);
            String middleInitial = fullNameRegularOrderMatcher.group(2);
            String lastName = fullNameRegularOrderMatcher.group(3);
            return new Name(trimmedInput, firstName, middleInitial, lastName, null, detectedSuffix);
        }

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
