package org.center4racialjustice.legup.illinois;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.domain.NameParser;
import org.center4racialjustice.legup.domain.VoteSide;
import org.center4racialjustice.legup.util.Lists;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BillVotesParser {

    private static final Logger log = LogManager.getLogger(BillVotesParser.class);

    private static final List<String> ignoreLines =
            Arrays.asList("Denotes Excused Absence");

    private static final List<String> hundredthLines =
            Arrays.asList("ONE HUNDREDTH", "100th General Assembly");

    private final NameParser nameParser;

    public BillVotesParser(NameParser nameParser){
        this.nameParser = nameParser;
    }

    public static BillVotes readFromUrlAndParse(String url, NameParser nameParser) {
        String contents = BillVotesParser.readFileFromUrl(url);
        BillVotesParser parser = new BillVotesParser(nameParser);
        return  parser.parseFileContents(url, contents);
    }

    public static String readFileFromUrl(String url) {
        log.info("Searching " + url);
        try {
            PDDocument doc = new PDDocument();
            doc.close();

            URL url_ = new URL(url);

            HttpURLConnection connection = (HttpURLConnection) url_.openConnection();
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            doc = PDDocument.load(inputStream);
            PDFTextStripper stripper = new PDFTextStripper();
            String content = stripper.getText(doc);
            doc.close();
            inputStream.close();
            return content;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
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

    private static Pattern committeeChamberPattern = Pattern.compile("(HOUSE|SENATE) COMMITTEE ROLL CALL");
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

    public static List<Integer> findPossibleDividingPoints(String voteLine){
        String[] markers = { "N ", "Y ", "P ", "NV ", "E ", "A "};
        List<Integer> points = new ArrayList<>();
        int point = 0;
        while( point < voteLine.length() ){
            for(String marker : markers) {
                if( voteLine.startsWith(marker, point) ){
                    points.add(point);
                }
            }
            point++;
        }
        return points;
    }

    public static List<Integer> findVoteLineDividingPoints(List<String> voteLines){
        if( voteLines.size() == 0 ){
            return Collections.emptyList();
        }
        List<Integer> intersection = null;
        for( int idx=0 ; idx<voteLines.size() - 1 ; idx++ ){
            System.out.println("vote line " + voteLines.get(idx));
            List<Integer> possibleDividingPoints = findPossibleDividingPoints(voteLines.get(idx));
            //System.out.println("        Possible dividers " + possibleDividingPoints);
            if( idx == 0 ) {
                intersection = possibleDividingPoints;
            } else {
                intersection.retainAll(possibleDividingPoints);
            }
        }
        return intersection;
    }

    private VoteRecord parseVoteRecordChunk(String chunk){
        int spaceIndex = chunk.indexOf(' ');
        String prefix = chunk.substring(0, spaceIndex);
        VoteSide vote = VoteSide.fromCode(prefix);
        String nameString = chunk.substring(spaceIndex);
        Name name = nameParser.fromLastNameFirstString(nameString);
        VoteRecord voteRecord = new VoteRecord(name, vote);
        return voteRecord;
    }

    private static boolean isPossibleSplitPoint(String input, int point){
        if( point > input.length() ){
            return false;
        }
        for(String marker : VoteSide.AllCodes){
            if( input.startsWith(marker + " ", point) ){
                return true;
            }
        }
        return false;
    }

    public static List<String> splitVotingLine(String input){
        List<String> chunks = new ArrayList<>();
        int recordStart = 0;
        for(int idx=0; idx<input.length(); idx++){
            if( isPossibleSplitPoint(input, idx) ){
                // check for names ending with an initial that matches a vote code
                if( ! isPossibleSplitPoint(input, idx+2) ){
                    String chunk = input.substring(recordStart, idx);
                    if( chunk.length() > 0 ){
                        chunks.add(chunk);
                    }
                    recordStart = idx;
                }
            }
        }
        chunks.add(input.substring(recordStart));
        return chunks;
    }

    public List<VoteRecord> parseVoteRecordLine(String input, List<Integer> dividers){
        List<VoteRecord> records = new ArrayList<>();
        List<String> chunks = new ArrayList<>();
        if( dividers.size() == 0 ){
            chunks.add(input);
        } else {
            int startPoint = dividers.get(0);
            int endPoint;
            for (int idx = 1; idx < dividers.size(); idx++) {
                endPoint = dividers.get(idx);
                if( endPoint > input.length()){
                    break;
                }
                chunks.add(input.substring(startPoint, endPoint));
                startPoint = endPoint;
            }
            String remainder = input.substring(startPoint).trim();
            if( remainder.length() > 0 ) {
                chunks.add(remainder);
            }
        }
        for(String chunk : chunks){
            int spaceIndex = chunk.indexOf(' ');
            String prefix = chunk.substring(0, spaceIndex);
            VoteSide vote = VoteSide.fromCode(prefix);
            String nameString = chunk.substring(spaceIndex);
            Name name = nameParser.fromLastNameFirstString(nameString);
            VoteRecord voteRecord = new VoteRecord(name, vote);
            records.add(voteRecord);
        }
        return records;
    }

    public static BillVotes parseFile(String filename, NameParser nameParser) {
        String content = readFileToString(filename);
        BillVotesParser parser = new BillVotesParser(nameParser);
        return parser.parseFileContents("",content);
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

    public BillVotes parseFileContents(String url, String content){
        String[] lines = content.split("\n");
        BillVotes bv = new BillVotes(url, content);

        List<String> voteLines = new ArrayList<>();

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

            Matcher committeeChamberMatcher = committeeChamberPattern.matcher(line);
            if( committeeChamberMatcher.matches()){
                String assemblyString = committeeChamberMatcher.group(1);
                Chamber chamber = Chamber.fromString(assemblyString);
                bv.setVotingChamber(chamber);
            }

            Matcher voteLineMatcher = voteLinePattern.matcher(line);
            if( voteLineMatcher.matches() ){
                System.out.println("VOTE LINE: " + line);
                List<String> chunks = splitVotingLine(line);
                System.out.println("  CHUNKS:  " + chunks);
                List<VoteRecord> records = Lists.map(chunks, this::parseVoteRecordChunk);
                System.out.println("  RECORDS:  " + records);
                bv.addVoteRecords(records);
            }
        }

        return bv;
    }

}
