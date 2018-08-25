package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.domain.NameParser;
import org.center4racialjustice.legup.domain.VoteSide;
import org.junit.Test;
import org.junit.Assert;

import java.util.HashMap;
import java.util.List;

public class TestBillVotesParser {

    private final String bill8FileName = "/pdfs/10000SB0008_02082017_002000T.pdf";
    private final String bill3179FileName = "/pdfs/10000HB3179_05262017_048000T.pdf";
    private final String houseBill2771FileName =  "/pdfs/10000HB2771_04272017_028000T.pdf";
    private final String houseBill4324FileName =  "/pdfs/10000HB4324_04272018_065000T.pdf";
    private final String senateBill1781FileName = "/pdfs/10000SB1781_05302017_032000T.pdf";

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
    }

    @Test
    public void testBill4234(){
        BillVotes billVotes = BillVotesParser.parseFile(houseBill4324FileName);
        checkNonListFields(houseBill4324(), billVotes);
    }

    @Test
    public void testBill8(){
        BillVotes billVotes = BillVotesParser.parseFile(bill8FileName);
        checkNonListFields(senateBill8(), billVotes);
    }

    @Test
    public void testBill3179(){
        BillVotes billVotes = BillVotesParser.parseFile(bill3179FileName);
        checkNonListFields(houseBill3179(), billVotes);
    }

    @Test
    public void testBill2771(){
        BillVotes billVotes = BillVotesParser.parseFile(houseBill2771FileName);
        checkNonListFields(houseBill2771(), billVotes);
    }

    @Test
    public void testBill1781(){
        BillVotes billVotes = BillVotesParser.parseFile(senateBill1781FileName);
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
        List<VoteRecord> records = BillVotesParser.parseVoteRecordLine(input);
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
                new VoteRecord(clayborne, VoteSide.Yea),
                new VoteRecord(landek, VoteSide.Yea),
                new VoteRecord(oberweis, VoteSide.Present),
                new VoteRecord(vanpelt, VoteSide.Yea)
        };
        List<VoteRecord> records = BillVotesParser.parseVoteRecordLine(input);
        Assert.assertArrayEquals(expectedRecords, records.toArray());
    }

}
