package org.center4racialjustice.legup.illinois;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.domain.NameParser;
import org.center4racialjustice.legup.domain.VoteSide;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BillVotesParser {

    private static final NameParser nameParser = new NameParser(new HashMap<>());

    private static final List<String> ignoreLines =
            Arrays.asList("Denotes Excused Absence");

    private static final List<String> hundredthLines =
            Arrays.asList("ONE HUNDREDTH", "100th General Assembly");

    public static BillVotes readFromUrlAndParse(String url) throws IOException {
        String contents = BillVotesParser.readFileFromUrl(url);
        return  BillVotesParser.parseFileContents(url, contents);
    }

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
            if( fileStream != null) {
                fileStream.close();
            }
        }
    }

    private static Pattern billAssemblyAndNumberPattern = Pattern.compile("(Senate|House) Bill No. (\\d+) *");
    private static Pattern alternateBillAssemblyAndNumberPattern = Pattern.compile("(HOUSE|SENATE) BILL (\\d+) *");
    private static Pattern summaryPattern1 =
            Pattern.compile("(\\d+) YEAS (\\d+) NAYS (\\d+) PRESENT *");
    private static Pattern summaryPattern2 =
            Pattern.compile("YEAS NAYS PRESENT NOT VOTING(\\d+) (\\d+) (\\d+) (\\d+)");
    private static Pattern summaryPattern3 =
            Pattern.compile("(\\d+) YEAS (\\d+) PRESENT(\\d+) NAYS");

    private static Pattern voteLinePattern =
            Pattern.compile("(NV|Y|N|P|E|A) .*");

    public static boolean isVoteLine(String line){
        Matcher voteLineMatcher = voteLinePattern.matcher(line);
        return voteLineMatcher.matches();
    }

    public static int findNextPossibleRecordIndex(String input){
        String[] markers = { "N ", "Y ", "P ", "NV ", "E ", "A "};
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
        return parseFileContents("",content);
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

    private static Long extractSession(String line){
        for(String hundredthLine : hundredthLines){
            if( line.contains(hundredthLine)){
                return 100L;
            }
        }
        return null;
    }

    public static BillVotes parseFileContents(String url, String content){
        String[] lines = content.split("\n");
        BillVotes bv = new BillVotes(url, content);
        for(int idx=0; idx<lines.length; idx++){
            String line = lines[idx];

            boolean ignore = false;
            for(String ignoreLine : ignoreLines){
                if(line.contains(ignoreLine)){
                    ignore = true;
                }
            }
            if ( ignore ) {
                continue;
            }

            Long session = extractSession(line);
            if( session != null ){
                bv.setSession(session);
                continue;
            }

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

            Matcher summaryMatcher1 = summaryPattern1.matcher(line);
            if (summaryMatcher1.matches()){
                String yeas = summaryMatcher1.group(1);
                bv.setExpectedYeas(Integer.parseInt(yeas));
                String nays = summaryMatcher1.group(2);
                bv.setExpectedNays(Integer.parseInt(nays));
                String presents = summaryMatcher1.group(3);
                bv.setExpectedPresent(Integer.parseInt(presents));
                continue;
            }

            Matcher summaryMatcher2 = summaryPattern2.matcher(line);
            if(summaryMatcher2.matches()){
                String yeas = summaryMatcher2.group(1);
                bv.setExpectedYeas( Integer.parseInt(yeas));
                String nays = summaryMatcher2.group(2);
                bv.setExpectedNays(Integer.parseInt(nays));
                String presents = summaryMatcher2.group(3);
                bv.setExpectedPresent(Integer.parseInt(presents));
                String notVotings = summaryMatcher2.group(4);
                bv.setExpectedNotVoting(Integer.parseInt(notVotings));
                continue;
            }

            Matcher summaryMatcher3 = summaryPattern3.matcher(line);
            if (summaryMatcher3.matches()){
                String yeas = summaryMatcher3.group(1);
                bv.setExpectedYeas(Integer.parseInt(yeas));
                String presents = summaryMatcher3.group(2);
                bv.setExpectedPresent(Integer.parseInt(presents));
                String nays = summaryMatcher3.group(3);
                bv.setExpectedNays(Integer.parseInt(nays));
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
