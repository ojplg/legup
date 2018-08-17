package org.center4racialjustice.legup.illinois;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.domain.NameParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BillVotesParser {

    private static final NameParser nameParser = new NameParser(new HashMap<>());

    public static String readFileFromUrl(String url)
    throws IOException {
        PDDocument doc = new PDDocument();
        doc.close();

        URL url_ = new URL(url);

        HttpURLConnection connection = (HttpURLConnection) url_.openConnection();
        connection.connect();

        InputStream inputStream = connection.getInputStream();

        try {
            doc = PDDocument.load(inputStream);
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(doc);
        } finally {
            doc.close();
            inputStream.close();
        }
    }

    public static String readFileToString(String filename){
        try {
            return readFileToStringExceptions(filename);
        } catch (IOException ioe){
            throw new RuntimeException(ioe);
        }
    }

    public static String readFileToStringExceptions(String filename)
    throws IOException {
        PDDocument doc = new PDDocument();
        InputStream fileStream = null;
        try
        {
            doc.close();
            fileStream = doc.getClass().getResourceAsStream(filename);
            doc = PDDocument.load(fileStream);

            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(doc);
        }
        finally
        {
            doc.close();
            fileStream.close();
        }
    }

    private static Pattern billAssemblyAndNumberPattern = Pattern.compile("(Senate|House) Bill No. (\\d+) *");
    private static Pattern alternateBillAssemblyAndNumberPattern = Pattern.compile("(HOUSE|SENATE) BILL (\\d+) *");
    private static Pattern summaryPattern =
            Pattern.compile("(\\d+)\\s+YEAS\\s+(\\d+)\\s+NAYS\\s+(\\d+)\\s+PRESENT");
    private static Pattern alternateSummaryPattern =
            Pattern.compile("YEAS NAYS PRESENT NOT VOTING(\\d+) (\\d+) (\\d+) (\\d+)");

    private static Pattern voteLinePattern =
            Pattern.compile("(NV|Y|N|P|E) .*");

    public static boolean isVoteLine(String line){
        Matcher voteLineMatcher = voteLinePattern.matcher(line);
        return voteLineMatcher.matches();
    }

    public static int findNextPossibleRecordIndex(String input){
        String[] markers = { "N ", "Y ", "P ", "NV ", "E "};
        int earliestIndex = input.length();
        for(String marker : markers){
            int idx = input.indexOf(marker);
            if ( idx >= 0 && idx < earliestIndex ){
                earliestIndex = idx;
            }
        }
        if (earliestIndex == input.length()){
            return -1;
        } else {
            return earliestIndex;
        }
    }

    public static List<VoteRecord> parseVoteRecordLine(String input){
        String remainder = input.trim();
        List<VoteRecord> records = new ArrayList<>();
        while (remainder.length() > 0){
            int firstSpace = remainder.indexOf(' ');
            String prefix = remainder.substring(0, firstSpace);
            VoteSide vote = VoteSide.fromCode(prefix);
            String end = remainder.substring(firstSpace);
            int divider = findNextPossibleRecordIndex(end);
            if( divider == -1 ){
                Name name = nameParser.fromLastNameFirstString(end);
                VoteRecord record = new VoteRecord(name, vote);
                remainder = "";
                records.add(record);
            } else {
                String nameString = end.substring(0, divider);
                remainder = end.substring(divider);
                Name name = nameParser.fromLastNameFirstString(nameString);
                VoteRecord record = new VoteRecord(name, vote);
                records.add(record);
            }
        }
        return records;
    }

    public static BillVotes parseFile(String filename) {
        String content = readFileToString(filename);
        return parseFileContents(content);
    }

    private static String[] houseVoteStrings = new String[]{ "HOUSE ROLL CALL" };
    private static String[] senateVoteStrings = new String[]{ "Senate Vote", "Senate Committee Vote" };

    private static Chamber determineVotingChamber(String line){
        for(String houseVoteString : houseVoteStrings){
            if(line.contains(houseVoteString)){
                return Chamber.House;
            }
        }
        for(String senateVoteString : senateVoteStrings){
            if(line.contains(senateVoteString)){
                return Chamber.Senate;
            }
        }
        return null;
    }

    public static BillVotes parseFileContents(String content){
        String[] lines = content.split("\n");
        BillVotes bv = new BillVotes(content);
        for(int idx=0; idx<lines.length; idx++){
            String line = lines[idx];

            Chamber votingChamber = determineVotingChamber(line);
            if( votingChamber != null ){
                bv.setVotingChamber(votingChamber);
                continue;
            }

            Matcher billNumberMatcher = billAssemblyAndNumberPattern.matcher(line);
            if( billNumberMatcher.matches() ){
                String assemblyString = billNumberMatcher.group(1);
                String billNumberString = billNumberMatcher.group(2);
                int billNumber = Integer.parseInt(billNumberString);
                Chamber chamber = Chamber.fromString(assemblyString);
                bv.setBillNumber(billNumber);
                bv.setBillChamber(chamber);
                continue;
            }
            Matcher alternateBillNumberMatcher = alternateBillAssemblyAndNumberPattern.matcher(line);
            if( alternateBillNumberMatcher.matches() ){
                String assemblyString = alternateBillNumberMatcher.group(1);
                String billNumberString = alternateBillNumberMatcher.group(2);
                int billNumber = Integer.parseInt(billNumberString);
                Chamber chamber = Chamber.fromString(assemblyString);
                bv.setBillNumber(billNumber);
                bv.setBillChamber(chamber);
                continue;
            }

            Matcher summaryMatcher = summaryPattern.matcher(line);
            while (summaryMatcher.find()){
                String yeas = summaryMatcher.group(1);
                bv.setExpectedYeas(Integer.parseInt(yeas));
                String nays = summaryMatcher.group(2);
                bv.setExpectedNays(Integer.parseInt(nays));
                String presents = summaryMatcher.group(3);
                bv.setExpectedPresent(Integer.parseInt(presents));
                continue;
            }

            Matcher alternateSummaryMatcher = alternateSummaryPattern.matcher(line);
            while(alternateSummaryMatcher.find()){
                String yeas = alternateSummaryMatcher.group(1);
                bv.setExpectedYeas( Integer.parseInt(yeas));
                String nays = alternateSummaryMatcher.group(2);
                bv.setExpectedNays(Integer.parseInt(nays));
                String presents = alternateSummaryMatcher.group(3);
                bv.setExpectedPresent(Integer.parseInt(presents));
                String notVotings = alternateSummaryMatcher.group(4);
                bv.setExpectedNotVoting(Integer.parseInt(notVotings));
                continue;
            }

            Matcher voteLineMatcher = voteLinePattern.matcher(line);
            if( voteLineMatcher.matches() ){
                List<VoteRecord> records = parseVoteRecordLine(line);
                for(VoteRecord record : records ){
                    bv.addVoteRecord(record);
                }
            }
        }

        return bv;
    }


}
