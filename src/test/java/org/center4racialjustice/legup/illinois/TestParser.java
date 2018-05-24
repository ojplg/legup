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

//    @Test
//    public void testVoteCollections(){
//        /*
//        NV Barickman, Jason A NV Brady, William E
//        Y Clayborne Jr., James F NV Cullerton, John J
//        Y Harmon, Don Y Hunter, Mattie
//        Y Lightford, Kimberly A Y Link, Terry
//        Y Mulroe, John G NV Muñoz, Antonio
//        Y Nybo, Chris Y Oberweis, Jim
//        NV Raoul, Kwame Y Rezin, Sue
//        NV Silverstein, Ira I NV Steans, Heather A
//        NV Syverson, Dave
//        */
//        Name[] expectedNotVoting =
//                {
//                        Name.fromFirstLastMiddleInitial("Jason","Barickman", "A"),
//                        Name.fromFirstLastMiddleInitial("William", "Brady", "E"),
//                        Name.fromFirstLastMiddleInitial("Cullerton", "John", "J"),
//                        Name.fromFirstLast("Kwame", "Raoul"),
//                        Name.fromFirstLast("Antonio","Muñoz"),
//                        Name.fromFirstLastMiddleInitial("Ira", "Silverstein", "I"),
//                        Name.fromFirstLastMiddleInitial("Heather", "Steans", "A"),
//                        Name.fromFirstLast("Dave","Syverson")
//                };
//
//        Name[] expectedYeas =
//                {
//                        new Name("James","F", "Clayborn",null, "Jr"),
//                        Name.fromFirstLast("Don", "Harmon"),
//                        Name.fromFirstLast("Mattie","Hunter"),
//                        Name.fromFirstLastMiddleInitial("Kimberly", "Lightford", "A"),
//                        Name.fromFirstLast("Terry", "Link"),
//                        Name.fromFirstLast("John", "Mulroe"),
//                        Name.fromFirstLast("Chris", "Nybo"),
//                        Name.fromFirstLast("Jim", "Oberweis"),
//                        Name.fromFirstLast("Sue","Rezin")
//                };
//
//        BillVotes bv = Parser.parseFile(bill2781FileName);
//        Assert.assertArrayEquals(expectedNotVoting, bv.notVotings.toArray(new Name[8]));
//        Assert.assertArrayEquals(expectedYeas, bv.yeas.toArray(new Name[9]));
//    }

    @Test
    public void senateBillNumberNotAVoteLine(){
        Assert.assertFalse(
                Parser.isVoteLine("Senate Bill No. 8"));
    }

    @Test
    public void stateOfIllinoisNotAVoteLine(){
        Assert.assertFalse(Parser.isVoteLine("State of Illinois"));
    }

    @Test
    public void singleNotVoteIsAVoteLine(){
        Assert.assertTrue(Parser.isVoteLine("NV Syverson, Dave"));
    }

    @Test
    public void complexNamesCanBeVoteLines(){
        Assert.assertTrue(Parser.isVoteLine("Y Clayborne Jr., James F NV Cullerton, John J"));
    }

    @Test
    public void longNamesCanBeVoteLines(){
        Assert.assertTrue(Parser.isVoteLine("NV Silverstein, Ira I NV Steans, Heather A"));
    }

    @Test
    public void parseVoteRecord() {
        Name alfred = Name.fromFirstLast("Alfred", "Redblatt");
        VoteRecord expected = new VoteRecord(alfred, Vote.Yea);
        String input = "Y Redblatt, Alfred";
        VoteRecord parsed = Parser.parseVoteRecord(input);
        Assert.assertEquals(expected, parsed);
    }

}
