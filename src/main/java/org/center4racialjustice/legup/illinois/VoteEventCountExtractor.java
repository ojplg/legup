package org.center4racialjustice.legup.illinois;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VoteEventCountExtractor implements VoteEventCounts {

    private static final Pattern VOTE_COUNT_PATTERN =
            Pattern.compile(".*(\\d\\d\\d)-(\\d\\d\\d)-(\\d\\d\\d)");

    private final String rawEventDescription;

    private final int yeaCount;
    private final int nayCount;
    private final int otherCount;

    public VoteEventCountExtractor(String rawEventDescription) {
        this.rawEventDescription = rawEventDescription;
        Matcher matcher = VOTE_COUNT_PATTERN.matcher(rawEventDescription);
        if( matcher.matches() ){
            yeaCount = Integer.parseInt(matcher.group(1));
            nayCount = Integer.parseInt(matcher.group(2));
            otherCount = Integer.parseInt(matcher.group(3));
        } else {
            throw new RuntimeException("Could not parse counts from " + rawEventDescription);
        }
    }

    public int getYeaCount(){
        return yeaCount;
    }

    public int getNayCount(){
        return nayCount;
    }

    public int getOtherCount(){
        return otherCount;
    }

}
