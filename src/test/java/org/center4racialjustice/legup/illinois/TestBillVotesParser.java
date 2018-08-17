package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.domain.NameParser;
import org.junit.Test;
import org.junit.Assert;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class TestBillVotesParser {

    private final String bill8FileName = "/pdfs/10000SB0008_02082017_002000T.pdf";
    private final String bill2781FileName = "/pdfs/10000SB2781_19952.pdf";
    private final String bill3179FileName = "/pdfs/10000HB3179_05262017_048000T.pdf";
    private final String houseBill2771FileName =  "/pdfs/10000HB2771_04272017_028000T.pdf";

    @Test
    public void testSomeHouseBill(){
        BillVotes billVotes = BillVotesParser.parseFile(houseBill2771FileName);
        Assert.assertNotNull(billVotes);
    }

    @Test
    public void testGetAssembly_House(){
        BillVotes billVotes = BillVotesParser.parseFile(bill3179FileName);
        Assert.assertEquals(Chamber.House, billVotes.getBillChamber());;
    }

    @Test
    public void testGetAssembly_Senate(){
        BillVotes billVotes = BillVotesParser.parseFile(bill2781FileName);
        Assert.assertEquals(Chamber.Senate, billVotes.getBillChamber());
    }


    @Test
    public void testGetBillNumber_House(){
        BillVotes billVotes = BillVotesParser.parseFile(bill3179FileName);
        Assert.assertEquals(3179L, billVotes.getBillNumber());;
    }

    @Test
    public void testGetBillNumber_Senate(){
        BillVotes billVotes = BillVotesParser.parseFile(bill2781FileName);
        Assert.assertEquals(2781L, billVotes.getBillNumber());
    }

    @Test
    public void testGetBillNumber_House_Alternate(){
        BillVotes billVotes = BillVotesParser.parseFile(houseBill2771FileName);
        Assert.assertEquals( billVotes.getContent(),2771L, billVotes.getBillNumber());;
    }


    @Test
    public void testFileLoading(){
        String content = BillVotesParser.readFileToString(bill8FileName);
        Assert.assertTrue(content.length() > 1);
        String next = BillVotesParser.readFileToString(bill2781FileName);
        Assert.assertTrue(next.length() > 1);
    }

    @Test
    public void testBillNumber8(){
        BillVotes bv = BillVotesParser.parseFile(bill8FileName);
        Assert.assertEquals(8, bv.getBillNumber());
    }

    @Test
    public void testBillNumber2781(){
        BillVotes bv = BillVotesParser.parseFile(bill2781FileName);
        Assert.assertEquals(2781, bv.getBillNumber());
    }

    @Test
    public void testExpectedCounts8(){
        BillVotes bv = BillVotesParser.parseFile(bill8FileName);
        Assert.assertEquals(34, bv.getExpectedYeas());
        Assert.assertEquals(14, bv.getExpectedNays());
        Assert.assertEquals(11, bv.getExpectedPresent());
        Assert.assertEquals(0, bv.getExpectedNotVoting());
    }

    @Test
    public void testExpectedCounts2781(){
        BillVotes bv = BillVotesParser.parseFile(bill2781FileName);
        Assert.assertEquals(9, bv.getExpectedYeas() );
        Assert.assertEquals(0, bv.getExpectedNays());
        Assert.assertEquals(0, bv.getExpectedPresent());
        Assert.assertEquals(8, bv.getExpectedNotVoting());
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

        BillVotes bv = BillVotesParser.parseFile(bill2781FileName);
        Assert.assertArrayEquals(expectedNotVoting, bv.getNotVotings().toArray(new Name[8]));
        Assert.assertArrayEquals(expectedYeas, bv.getYeas().toArray(new Name[9]));
    }

    @Test
    public void checkVoteCountsWorks(){
        BillVotes bv = BillVotesParser.parseFile(bill2781FileName);
        bv.checkVoteCounts();
    }

    @Test
    public void senateBillNumberNotAVoteLine(){
        Assert.assertFalse(
                BillVotesParser.isVoteLine("Senate Bill No. 8"));
    }

    @Test
    public void stateOfIllinoisNotAVoteLine(){
        Assert.assertFalse(BillVotesParser.isVoteLine("State of Illinois"));
    }

    @Test
    public void singleNotVoteIsAVoteLine(){
        Assert.assertTrue(BillVotesParser.isVoteLine("NV Syverson, Dave"));
    }

    @Test
    public void complexNamesCanBeVoteLines(){
        Assert.assertTrue(BillVotesParser.isVoteLine("Y Clayborne Jr., James F NV Cullerton, John J"));
    }

    @Test
    public void longNamesCanBeVoteLines(){
        Assert.assertTrue(BillVotesParser.isVoteLine("NV Silverstein, Ira I NV Steans, Heather A"));
    }

    @Test
    public void parseVoteRecordLine() {
        Name alfred = Name.fromFirstLast("Alfred", "Redblatt");
        VoteRecord expected1 = new VoteRecord(alfred, Vote.Yea);
        NameParser nameParser = new NameParser(new HashMap<>());
        Name james = nameParser.fromLastNameFirstString("Clayborne Jr., James F");
        VoteRecord expected2 = new VoteRecord(james, Vote.NotVoting);
        String input = "Y Redblatt, Alfred NV Clayborne Jr., James F";
        List<VoteRecord> records = BillVotesParser.parseVoteRecordLine(input);
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
        List<VoteRecord> records = BillVotesParser.parseVoteRecordLine(input);
        VoteRecord[] expectedRecords = new VoteRecord[] { v1, v2 };
        Assert.assertArrayEquals(expectedRecords, records.toArray());
    }

    @Test
    public void parseVoteRecordLineWithGoofballLastName(){
        String input = "Y  Clayborne       Y  Landek     P  Oberweis     Y  Van Pelt";
        NameParser nameParser = new NameParser(new HashMap<>());
        Name clayborne = nameParser.fromLastNameFirstString("Clayborne");
        Name landek = nameParser.fromLastNameFirstString("Landek");
        Name oberweis = nameParser.fromLastNameFirstString("Oberweis");
        Name vanpelt = nameParser.fromLastNameFirstString("Van Pelt");
        VoteRecord[] expectedRecords = new VoteRecord[] {
                new VoteRecord(clayborne, Vote.Yea),
                new VoteRecord(landek, Vote.Yea),
                new VoteRecord(oberweis, Vote.Present),
                new VoteRecord(vanpelt, Vote.Yea)
        };
        List<VoteRecord> records = BillVotesParser.parseVoteRecordLine(input);
        Assert.assertArrayEquals(expectedRecords, records.toArray());
    }

    @Test
    public void houseBillCanBeParsed(){
        BillVotes bv = BillVotesParser.parseFile(bill3179FileName);
        bv.checkVoteCounts();
    }

    @Test
    public void testReportsCorrectVotingChamber_2771(){
        BillVotes bv = BillVotesParser.parseFile(houseBill2771FileName);
        Assert.assertEquals(Chamber.House, bv.getVotingChamber());
    }

    @Test
    public void testReportsCorrectVotingChamber_3179(){
        BillVotes bv = BillVotesParser.parseFile(bill3179FileName);
        Assert.assertEquals(Chamber.Senate, bv.getVotingChamber());
    }

    @Test
    public void testReportsCorrectVotingChamber_8(){
        BillVotes bv = BillVotesParser.parseFile(bill8FileName);
        Assert.assertEquals(Chamber.Senate, bv.getVotingChamber());
    }

    @Test
    public void testReportsCorrectVotingChamber_2781(){
        BillVotes bv = BillVotesParser.parseFile(bill2781FileName);
        Assert.assertEquals(Chamber.Senate, bv.getVotingChamber());
    }

    @Test
    public void readOverHttp() throws IOException  {
        String url = "http://www.ilga.gov/legislation/votehistory/100/senate/10000SB0001_05172017_009000T.pdf";

        String contents = BillVotesParser.readFileFromUrl(url);

        Assert.assertNotNull(contents);
    }
}
