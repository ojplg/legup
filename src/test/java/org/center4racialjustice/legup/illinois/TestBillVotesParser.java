package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.domain.NameOverrides;
import org.center4racialjustice.legup.domain.NameParser;
import org.center4racialjustice.legup.domain.VoteSide;
import org.center4racialjustice.legup.domain.VoteType;
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
    private final String house101Bill2040CommitteeFileName = "/pdfs/10100HB2040HFA3_25107.pdf";

    private static NameParser loadNameParser(){
        NameOverrides nameOverrides =  NameOverrides.load("conf/name.overrides");
        return new NameParser(nameOverrides.getOverrides());
    }

    private static BillVotes houseBill4324(){
        BillIdentity  billIdentity = new BillIdentity(100L, Chamber.House, null, 4324L );
        ExpectedVoteCounts expectedVoteCounts = ExpectedVoteCounts.builder()
                .expectedYeas(88)
                .expectedNays(3)
                .expectedPresent(0)
                .build();

        BillVotes billVotes = new BillVotes(billIdentity, null, expectedVoteCounts, null, Chamber.House);
        return billVotes;
    }

    private BillVotes senateBill8(){
        BillIdentity  billIdentity = new BillIdentity(100L, Chamber.Senate, null,  8L);
        ExpectedVoteCounts expectedVoteCounts = ExpectedVoteCounts.builder()
                .expectedYeas(34)
                .expectedNays(14)
                .expectedPresent(11)
                .build();

        BillVotes billVotes = new BillVotes(billIdentity, null, expectedVoteCounts, null, Chamber.Senate);
        return billVotes;
    }

    private BillVotes houseBill3179(){
        BillIdentity  billIdentity = new BillIdentity(100L, Chamber.House,null, 3179L);
        ExpectedVoteCounts expectedVoteCounts = ExpectedVoteCounts.builder()
                .expectedYeas(48)
                .expectedNays(0)
                .expectedPresent(0)
                .build();

        BillVotes billVotes = new BillVotes(billIdentity, null, expectedVoteCounts, null, Chamber.Senate);
        return billVotes;
    }

    private BillVotes houseBill2771(){
        BillIdentity  billIdentity = new BillIdentity(100L, Chamber.House, null, 2771L);
        ExpectedVoteCounts expectedVoteCounts = ExpectedVoteCounts.builder()
                .expectedYeas(66)
                .expectedNays(51)
                .expectedPresent(0)
                .build();

        BillVotes billVotes = new BillVotes(billIdentity, null, expectedVoteCounts, null, Chamber.House);
        return billVotes;
    }

    private BillVotes senateBill1781(){
        BillIdentity  billIdentity = new BillIdentity(100L, Chamber.Senate,  null, 1781L);
        ExpectedVoteCounts expectedVoteCounts = ExpectedVoteCounts.builder()
                .expectedYeas(61)
                .expectedNays(55)
                .expectedPresent(0)
                .build();

        BillVotes billVotes = new BillVotes(billIdentity, null, expectedVoteCounts, null, Chamber.House);
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
        BillVotes billVotes = BillVotesParser.parseFile(senateBillOne, loadNameParser(), new VoteType("Third Reading"));
        billVotes.checkVoteCounts();
        Assert.assertEquals(69, billVotes.getYeas().size());
        Assert.assertEquals(47, billVotes.getNays().size());
    }

    @Test
    public void testBill4234(){
        BillVotes billVotes = BillVotesParser.parseFile(houseBill4324FileName, loadNameParser(), new VoteType("Third Reading"));
        checkNonListFields(houseBill4324(), billVotes);
    }

    @Test
    public void testBill8(){
        BillVotes billVotes = BillVotesParser.parseFile(bill8FileName, loadNameParser(), new VoteType("Third Reading"));
        checkNonListFields(senateBill8(), billVotes);
    }

    @Test
    public void testBill3179(){
        BillVotes billVotes = BillVotesParser.parseFile(bill3179FileName, loadNameParser(), new VoteType("Third Reading"));
        checkNonListFields(houseBill3179(), billVotes);
    }

    @Test
    public void testBill2771(){
        BillVotes billVotes = BillVotesParser.parseFile(houseBill2771FileName, loadNameParser(), new VoteType("Third Reading"));
        checkNonListFields(houseBill2771(), billVotes);
    }

    @Test
    public void testBill1781(){
        BillVotes billVotes = BillVotesParser.parseFile(senateBill1781FileName, loadNameParser(), new VoteType("Third Reading"));
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
        BillVotes billVotes = BillVotesParser.parseFile(house101Bill3704SenateCommitteeFileName, loadNameParser(), new VoteType("Third Reading"));
        billVotes.checkVoteCounts();
        Assert.assertEquals(10, billVotes.getYeas().size());
        Assert.assertEquals(Chamber.Senate, billVotes.getVotingChamber());
        Assert.assertEquals(Chamber.House, billVotes.getBillChamber());
        Assert.assertEquals(3704L, billVotes.getBillNumber());
        Assert.assertEquals(101L, billVotes.getSession());
    }

    @Test
    public void parse101House2045Bill_Committee(){
        BillVotes billVotes = BillVotesParser.parseFile(house101Bill2045CommitteeFileName, loadNameParser(), new VoteType("Third Reading"));
        billVotes.checkVoteCounts();
        Assert.assertEquals(6, billVotes.getYeas().size());
        Assert.assertEquals(Chamber.House, billVotes.getVotingChamber());
        Assert.assertEquals(2045L, billVotes.getBillNumber());
        Assert.assertEquals(101L, billVotes.getSession());
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

    @Test
    public void testGoodParse_101House2040(){
        BillVotes billVotes = BillVotesParser.parseFile(house101Bill2040CommitteeFileName, loadNameParser(), new VoteType(""));
        billVotes.checkVoteCounts();
        Assert.assertEquals(17, billVotes.getYeas().size());
    }
}
