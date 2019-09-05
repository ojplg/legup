package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Chamber;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;

public class TestVoteLinkInfo {

    @Test
    public void testSomeDateParsing(){
        VoteLinkInfo voteLinkInfo = VoteLinkInfo.create("HB2045 - Third Reading - Tuesday, April 2, 2019",
                Chamber.Senate, false, "url");

        Assert.assertEquals(LocalDate.of(2019, 4, 2), voteLinkInfo.getVoteDate());
    }

    @Test
    public void testSomeMatchingRegex(){
        VoteLinkInfo voteLinkInfo = VoteLinkInfo.create("HB2244 - Judiciary - Criminal - Mar 05, 2019",
                Chamber.House, true, "url");

        Assert.assertNotNull(voteLinkInfo);
        Assert.assertEquals("Judiciary - Criminal", voteLinkInfo.getVoteDescription());
    }

    @Test
    public void testIgnoresWordyDayOfWeekWithoutQualifier(){
        VoteLinkInfo voteLinkInfo = VoteLinkInfo.create("HB1115 - Thursday, April 11, 2019",
                Chamber.House, false, "url");


        Assert.assertNotNull(voteLinkInfo);
        Assert.assertEquals(LocalDate.of(2019, 4, 11), voteLinkInfo.getVoteDate());

    }
}
