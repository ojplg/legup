package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.domain.NameOverrides;
import org.center4racialjustice.legup.domain.NameParser;
import org.center4racialjustice.legup.domain.VoteSide;
import org.junit.Test;
import org.junit.Assert;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TestBillVotesParser {

    static {
        System.setProperty("java.util.logging.manager","org.apache.logging.log4j.jul.LogManager");
    }

    private final String bill8FileName = "/pdfs/10000SB0008_02082017_002000T.pdf";
    private final String bill3179FileName = "/pdfs/10000HB3179_05262017_048000T.pdf";
    private final String houseBill2771FileName =  "/pdfs/10000HB2771_04272017_028000T.pdf";
    private final String houseBill4324FileName =  "/pdfs/10000HB4324_04272018_065000T.pdf";
    private final String senateBill1781FileName = "/pdfs/10000SB1781_05302017_032000T.pdf";
    private final String house101Bill3704SenateCommitteeFileName = "/pdfs/10100HB3704_25968.pdf";
    private final String senateBillOne = "/pdfs/10100SB0001_02142019_003000T.pdf";
    private final String house101Bill2045CommitteeFileName = "/pdfs/10100HB2045_23584.pdf";

    private static NameParser loadNameParser(){
        NameOverrides nameOverrides =  NameOverrides.load("conf/name.overrides");
        return new NameParser(nameOverrides.getOverrides());
    }

    private static BillVotes houseBill4324(){
        BillVotes billVotes = new BillVotes();

        billVotes.setSession(100);
        billVotes.setVotingChamber(Chamber.House);
        billVotes.setBillChamber(Chamber.House);
        billVotes.setBillNumber(4324);
        billVotes.setExpectedYeas(88);
        billVotes.setExpectedNays(3);
        billVotes.setExpectedPresent(0);

        return billVotes;
    }

    private BillVotes senateBill8(){
        BillVotes billVotes = new BillVotes();

        billVotes.setSession(100);
        billVotes.setVotingChamber(Chamber.Senate);
        billVotes.setBillChamber(Chamber.Senate);
        billVotes.setBillNumber(8);
        billVotes.setExpectedYeas(34);
        billVotes.setExpectedNays(14);
        billVotes.setExpectedPresent(11);

        return billVotes;
    }

    private BillVotes houseBill3179(){
        BillVotes billVotes = new BillVotes();

        billVotes.setSession(100);
        billVotes.setVotingChamber(Chamber.Senate);
        billVotes.setBillChamber(Chamber.House);
        billVotes.setBillNumber(3179);
        billVotes.setExpectedYeas(48);
        billVotes.setExpectedNays(0);
        billVotes.setExpectedPresent(0);

        return billVotes;

    }

    private BillVotes houseBill2771(){
        BillVotes billVotes = new BillVotes();

        billVotes.setSession(100);
        billVotes.setVotingChamber(Chamber.House);
        billVotes.setBillChamber(Chamber.House);
        billVotes.setBillNumber(2771);
        billVotes.setExpectedYeas(66);
        billVotes.setExpectedNays(51);
        billVotes.setExpectedPresent(0);

        return billVotes;
    }

    private BillVotes senateBill1781(){
        BillVotes billVotes = new BillVotes();

        billVotes.setSession(100);
        billVotes.setVotingChamber(Chamber.House);
        billVotes.setBillChamber(Chamber.Senate);
        billVotes.setBillNumber(1781);
        billVotes.setExpectedYeas(61);
        billVotes.setExpectedNays(55);
        billVotes.setExpectedPresent(0);

        return billVotes;
    }

    private void checkNonListFields(BillVotes expected, BillVotes tested){
        Assert.assertEquals("Unmatched session", expected.getSession(), tested.getSession());
        Assert.assertEquals("Unmatched voting chamber", expected.getVotingChamber(), tested.getVotingChamber());
        Assert.assertEquals("Unmatched bill number", expected.getBillNumber(), tested.getBillNumber());
        Assert.assertEquals("Unmatched expected yeas", expected.getExpectedYeas(), tested.getExpectedYeas());
        Assert.assertEquals("Unmatched expected nays", expected.getExpectedNays(), tested.getExpectedNays());
        Assert.assertEquals("Unmatched expected present", expected.getExpectedPresent(), tested.getExpectedPresent());
        tested.checkVoteCounts();
    }

    @Test
    public void testHouseVotesOnConstitutionalAmendment(){
        BillVotes billVotes = BillVotesParser.parseFile(senateBillOne, loadNameParser());
        billVotes.checkVoteCounts();
        Assert.assertEquals(69, billVotes.getYeas().size());
        Assert.assertEquals(47, billVotes.getNays().size());
    }

    @Test
    public void testBill4234(){
        BillVotes billVotes = BillVotesParser.parseFile(houseBill4324FileName, loadNameParser());
        checkNonListFields(houseBill4324(), billVotes);
    }

    @Test
    public void testBill8(){
        BillVotes billVotes = BillVotesParser.parseFile(bill8FileName, loadNameParser());
        checkNonListFields(senateBill8(), billVotes);
    }

    @Test
    public void testBill3179(){
        BillVotes billVotes = BillVotesParser.parseFile(bill3179FileName, loadNameParser());
        checkNonListFields(houseBill3179(), billVotes);
    }

    @Test
    public void testBill2771(){
        BillVotes billVotes = BillVotesParser.parseFile(houseBill2771FileName, loadNameParser());
        checkNonListFields(houseBill2771(), billVotes);
    }

    @Test
    public void testBill1781(){
        BillVotes billVotes = BillVotesParser.parseFile(senateBill1781FileName, loadNameParser());
        checkNonListFields(senateBill1781(), billVotes);
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
        VoteRecord expected1 = new VoteRecord(alfred, VoteSide.Yea);
        NameParser nameParser = new NameParser(new HashMap<>());
        Name james = nameParser.fromLastNameFirstString("Clayborne Jr., James F");
        VoteRecord expected2 = new VoteRecord(james, VoteSide.NotVoting);
        String input = "Y Redblatt, Alfred NV Clayborne Jr., James F";

        BillVotesParser parser = new BillVotesParser(loadNameParser());

        List<Integer> dividers = BillVotesParser.findPossibleDividingPoints(input);
        List<VoteRecord> records = parser.parseVoteRecordLine(input, dividers);
        VoteRecord[] expectedRecords = new VoteRecord[]{expected1, expected2};
        Assert.assertArrayEquals(expectedRecords, records.toArray());
    }

    @Test
    public void parseVoteRecordLineWithSpecialCharacterInName(){
        String input = "Y Nybo, Chris Y Oberweis, Jim";
        Name chris = Name.fromFirstLast("Chris", "Nybo");
        Name jim = Name.fromFirstLast("Jim", "Oberweis");
        VoteRecord v1 = new VoteRecord(chris, VoteSide.Yea);
        VoteRecord v2 = new VoteRecord(jim, VoteSide.Yea);
        BillVotesParser parser = new BillVotesParser(loadNameParser());
        List<Integer> dividers = BillVotesParser.findPossibleDividingPoints(input);
        Assert.assertEquals(Arrays.asList(0, 14), dividers);
        List<VoteRecord> records = parser.parseVoteRecordLine(input, dividers);
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
                new VoteRecord(clayborne, VoteSide.Yea),
                new VoteRecord(landek, VoteSide.Yea),
                new VoteRecord(oberweis, VoteSide.Present),
                new VoteRecord(vanpelt, VoteSide.Yea)
        };
        BillVotesParser parser = new BillVotesParser(loadNameParser());
        List<Integer> dividers = BillVotesParser.findPossibleDividingPoints(input);
        List<VoteRecord> records = parser.parseVoteRecordLine(input, dividers);
        Assert.assertArrayEquals(expectedRecords, records.toArray());
    }

    @Test
    public void parse101House3704Bill_Committee(){
        BillVotes billVotes = BillVotesParser.parseFile(house101Bill3704SenateCommitteeFileName, loadNameParser());
        billVotes.checkVoteCounts();
        Assert.assertEquals(10, billVotes.getYeas().size());
    }

    @Test
    public void parse101House2045Bill_Committee(){
        BillVotes billVotes = BillVotesParser.parseFile(house101Bill2045CommitteeFileName, loadNameParser());
        billVotes.checkVoteCounts();
        Assert.assertEquals(6, billVotes.getYeas().size());
        Assert.assertEquals(Chamber.House, billVotes.getVotingChamber());
    }

    @Test
    public void testFindPossibleDividingPoints(){
        //              012345678901234567890123456789012345678901234567890123456789
        String input = "Y  Clayborne       Y  Landek     P  Oberweis     Y  Van Pelt";
        List<Integer> calculatedDividingPoints = BillVotesParser.findPossibleDividingPoints(input);
        List<Integer> expectedDividingPoints = Arrays.asList(0, 19, 33, 49);
        Assert.assertEquals(expectedDividingPoints, calculatedDividingPoints);
    }


    @Test
    public void testFindPossibleDividingPoints_MultiLine(){
        //              012345678901234567890123456789012345678901234567890123456789
        String input1 = "Y  Clayborne       Y  Landek     P  Oberweis     Y  Van Pelt";
        String input2 = "Y  Clayborne N     Y  Landek     P  Oberweis     Y  Van Pelt";
        String input3 = "Y  Clayborne       Y  Landek A   P  Oberweis";
        List<Integer> expectedDividingPoints = Arrays.asList(0, 19, 33, 49);

        List<Integer> calculatedDividingPoints = BillVotesParser.findVoteLineDividingPoints(
                Arrays.asList(input1, input2, input3));
        Assert.assertEquals(expectedDividingPoints, calculatedDividingPoints);
    }

    @Test
    public void testDivideVoteLineIntoChunks(){
        String line = "Y  Ammons         Y  Didech          Y  Martwick      N  Sommer";
        List<String> chunks = BillVotesParser.splitVotingLine(line);
        Assert.assertEquals(4, chunks.size());
    }

}
