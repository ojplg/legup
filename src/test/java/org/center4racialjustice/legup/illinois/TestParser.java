package org.center4racialjustice.legup.illinois;

import org.junit.Test;
import org.junit.Assert;

public class TestParser {

    private final String bill8FileName = "/pdfs/10000SB0008_02082017_002000T.pdf";
    private final String bill2781FileName = "/pdfs/10000SB2781_19952.pdf";

    @Test
    public void testFileLoading(){
        String content = Parser.readFileToString(bill8FileName);
        System.out.println(content);
        String next = Parser.readFileToString(bill2781FileName);
        System.out.println(next);
    }

    @Test
    public void testBillNumber8(){
        BillVotes bv = Parser.parseFile(bill8FileName);
        Assert.assertEquals(8, bv.billNumber);
    }

    @Test
    public void testBillNumber2781(){
        BillVotes bv = Parser.parseFile(bill2781FileName);
        Assert.assertEquals(2781, bv.billNumber);
    }

    @Test
    public void testExpectedCounts8(){
        BillVotes bv = Parser.parseFile(bill8FileName);
        Assert.assertEquals(34, bv.expectedYeas);
        Assert.assertEquals(14, bv.expectedNays);
        Assert.assertEquals(11, bv.expectedPresent);
        Assert.assertEquals(0, bv.expectedNotVoting);
    }

    @Test
    public void testExpectedCounts2781(){
        BillVotes bv = Parser.parseFile(bill2781FileName);
        Assert.assertEquals(bv.content, 9, bv.expectedYeas );
        Assert.assertEquals(0, bv.expectedNays);
        Assert.assertEquals(0, bv.expectedPresent);
        Assert.assertEquals(8, bv.expectedNotVoting);
    }

}
