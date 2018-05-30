package org.center4racialjustice.legup.illinois;

import org.junit.Test;
import org.junit.Assert;

import java.util.List;

public class TestParser {

    private final String bill8FileName = "/pdfs/10000SB0008_02082017_002000T.pdf";
    private final String bill2781FileName = "/pdfs/10000SB2781_19952.pdf";
    private final String bill3179FileName = "/pdfs/10000HB3179_05262017_048000T.pdf";

    @Test
    public void testFileLoading(){
        String content = Parser.readFileToString(bill8FileName);
        Assert.assertTrue(content.length() > 1);
        String next = Parser.readFileToString(bill2781FileName);
        Assert.assertTrue(next.length() > 1);
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

    @Test
    public void testVoteCollections(){
        /*
        NV Barickman, Jason A NV Brady, William E
        Y Clayborne Jr., James F NV Cullerton, John J
        Y Harmon, Don Y Hunter, Mattie
        Y Lightford, Kimberly A Y Link, Terry
        Y Mulroe, John G NV Muñoz, Antonio
        Y Nybo, Chris Y Oberweis, Jim
        NV Raoul, Kwame Y Rezin, Sue
        NV Silverstein, Ira I NV Steans, Heather A
        NV Syverson, Dave
        */
        Name[] expectedNotVoting =
                {
                        Name.fromFirstLastMiddleInitial("Jason","Barickman", "A"),
                        Name.fromFirstLastMiddleInitial("William", "Brady", "E"),
                        Name.fromFirstLastMiddleInitial("John", "Cullerton", "J"),
                        Name.fromFirstLast("Antonio","Muñoz"),
                        Name.fromFirstLast("Kwame", "Raoul"),
                        Name.fromFirstLastMiddleInitial("Ira", "Silverstein", "I"),
                        Name.fromFirstLastMiddleInitial("Heather", "Steans", "A"),
                        Name.fromFirstLast("Dave","Syverson")
                };

        Name[] expectedYeas =
                {
                        new Name("James","F", "Clayborne",null, "Jr"),
                        Name.fromFirstLast("Don", "Harmon"),
                        Name.fromFirstLast("Mattie","Hunter"),
                        Name.fromFirstLastMiddleInitial("Kimberly", "Lightford", "A"),
                        Name.fromFirstLast("Terry", "Link"),
                        Name.fromFirstLastMiddleInitial("John", "Mulroe", "G"),
                        Name.fromFirstLast("Chris", "Nybo"),
                        Name.fromFirstLast("Jim", "Oberweis"),
                        Name.fromFirstLast("Sue","Rezin")
                };

        BillVotes bv = Parser.parseFile(bill2781FileName);
        Assert.assertArrayEquals(expectedNotVoting, bv.notVotings.toArray(new Name[8]));
        Assert.assertArrayEquals(expectedYeas, bv.yeas.toArray(new Name[9]));
    }

    @Test
    public void checkVoteCountsWorks(){
        BillVotes bv = Parser.parseFile(bill2781FileName);
        bv.checkVoteCounts();
    }

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
    public void parseVoteRecordLine() {
        Name alfred = Name.fromFirstLast("Alfred", "Redblatt");
        VoteRecord expected1 = new VoteRecord(alfred, Vote.Yea);
        Name james = Name.fromAnyString("Clayborne Jr., James F");
        VoteRecord expected2 = new VoteRecord(james, Vote.NotVoting);
        String input = "Y Redblatt, Alfred NV Clayborne Jr., James F";
        List<VoteRecord> records = Parser.parseVoteRecordLine(input);
        VoteRecord[] expectedRecords = new VoteRecord[]{expected1, expected2};
        Assert.assertArrayEquals(expectedRecords, records.toArray());
    }

    @Test
    public void parseVoteRecordLineWithSpecialCharacterInName(){
        String input = "Y Nybo, Chris Y Oberweis, Jim";
        Name chris = Name.fromFirstLast("Chris", "Nybo");
        Name jim = Name.fromFirstLast("Jim", "Oberweis");
        VoteRecord v1 = new VoteRecord(chris, Vote.Yea);
        VoteRecord v2 = new VoteRecord(jim, Vote.Yea);
        List<VoteRecord> records = Parser.parseVoteRecordLine(input);
        VoteRecord[] expectedRecords = new VoteRecord[] { v1, v2 };
        Assert.assertArrayEquals(expectedRecords, records.toArray());
    }

    @Test
    public void parseVoteRecordLineWithGoofballLastName(){
        String input = "Y  Clayborne       Y  Landek     P  Oberweis     Y  Van Pelt";
        Name clayborne = Name.fromAnyString("Clayborne");
        Name landek = Name.fromAnyString("Landek");
        Name oberweis = Name.fromAnyString("Oberweis");
        Name vanpelt = Name.fromAnyString("Van Pelt");
        VoteRecord[] expectedRecords = new VoteRecord[] {
                new VoteRecord(clayborne, Vote.Yea),
                new VoteRecord(landek, Vote.Yea),
                new VoteRecord(oberweis, Vote.Present),
                new VoteRecord(vanpelt, Vote.Yea)
        };
        List<VoteRecord> records = Parser.parseVoteRecordLine(input);
        Assert.assertArrayEquals(expectedRecords, records.toArray());
    }

    @Test
    public void houseBillCanBeParsed(){
        BillVotes bv = Parser.parseFile(bill3179FileName);
        bv.checkVoteCounts();
    }
}
