package org.center4racialjustice.legup.illinois;

import org.junit.Assert;
import org.junit.Test;

public class TestBadHtmlCleaner {

    @Test
    public void leavesGoodDatesAlone(){
        final String goodDate = "2/19/2019";
        final String cleanedDate = BadHtmlCleaner.cleanDateString(goodDate);

        Assert.assertEquals(goodDate, cleanedDate);
    }


    @Test
    public void cleansUpMisformattedPrefix(){
        final String badDate = "&p; 2/19/2019";
        final String cleanedDate = BadHtmlCleaner.cleanDateString(badDate);

        Assert.assertEquals("2/19/2019", cleanedDate);
    }
}
