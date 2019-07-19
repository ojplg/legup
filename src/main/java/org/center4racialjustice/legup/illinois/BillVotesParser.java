package org.center4racialjustice.legup.illinois;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.domain.NameParser;
import org.center4racialjustice.legup.domain.VoteSide;
import org.center4racialjustice.legup.domain.VoteType;
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

    private static final List<String> hundredFirstLines =
            Arrays.asList("101", "101th General Assembly");

    private final NameParser nameParser;

    public BillVotesParser(NameParser nameParser){
        this.nameParser = nameParser;
    }

    public static BillVotes readFromUrlAndParse(String url, NameParser nameParser) {
        String contents = BillVotesParser.readFileFromUrl(url);
        BillVotesParser parser = new BillVotesParser(nameParser);
        BillVotes billVotes = parser.parseFileContents(url, contents);
        log.info("Found vote records " + billVotes.getFullCount());
        return billVotes;
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
        return new VoteRecord(name, vote);
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

    public static BillVotes parseFile(String filename, NameParser nameParser, VoteType voteType) {
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
        for(String hundredFirstLine : hundredFirstLines){
            if( line.contains(hundredFirstLine)){
                return 101L;
            }
        }
        return null;
    }

    private BillIdentity parseBillIdentity(List<String> lines){
        long session = 0;
        Chamber chamber = null;
        long number = 0;

        for( String line : lines ) {
            Long sessionNumber = extractSession(line);
            if (sessionNumber != null) {
                session = sessionNumber;
                continue;
            }
            Matcher billNumberMatcher = billAssemblyAndNumberPattern.matcher(line);
            if (billNumberMatcher.matches()) {
                String assemblyString = billNumberMatcher.group(1);
                String billNumberString = billNumberMatcher.group(2);
                number = Integer.parseInt(billNumberString);
                chamber = Chamber.fromString(assemblyString);
                continue;
            }
            Matcher alternateBillNumberMatcher = alternateBillAssemblyAndNumberPattern.matcher(line);
            if( alternateBillNumberMatcher.matches() ){
                String assemblyString = alternateBillNumberMatcher.group(1);
                String billNumberString = alternateBillNumberMatcher.group(2);
                number = Integer.parseInt(billNumberString);
                chamber = Chamber.fromString(assemblyString);
            }
        }

        // FIXME: Legislation type?
        return new BillIdentity(session, chamber, null, number);
    }

    private ExpectedVoteCounts parseExpectedVoteCounts(List<String> lines){
        for(String line : lines){
            Matcher summaryMatcher1 = summaryPattern1.matcher(line);
            if (summaryMatcher1.matches()){
                String yeas = summaryMatcher1.group(1);
                String nays = summaryMatcher1.group(2);
                String presents = summaryMatcher1.group(3);
                return ExpectedVoteCounts.builder()
                        .expectedYeas(Integer.parseInt(yeas))
                        .expectedNays(Integer.parseInt(nays))
                        .expectedPresent(Integer.parseInt(presents))
                        .build();
            }

            Matcher summaryMatcher2 = summaryPattern2.matcher(line);
            if(summaryMatcher2.matches()){
                String yeas = summaryMatcher2.group(1);
                String nays = summaryMatcher2.group(2);
                String presents = summaryMatcher2.group(3);
                String notVotings = summaryMatcher2.group(4);
                return ExpectedVoteCounts.builder()
                        .expectedYeas(Integer.parseInt(yeas))
                        .expectedNays(Integer.parseInt(nays))
                        .expectedPresent(Integer.parseInt(presents))
                        .expectedNotVoting(Integer.parseInt(notVotings))
                        .build();
            }

            Matcher summaryMatcher3 = summaryPattern3.matcher(line);
            if (summaryMatcher3.matches()){
                String yeas = summaryMatcher3.group(1);
                String presents = summaryMatcher3.group(2);
                String nays = summaryMatcher3.group(3);
                return ExpectedVoteCounts.builder()
                        .expectedYeas(Integer.parseInt(yeas))
                        .expectedNays(Integer.parseInt(nays))
                        .expectedPresent(Integer.parseInt(presents))
                        .build();
            }
        }
        throw new RuntimeException("Could not find expected counts");
    }

    private Chamber parseVotingChamber(List<String> lines) {
        for (String line : lines) {
            Chamber votingChamber = determineVotingChamber(line);
            if( votingChamber != null ){
                return votingChamber;
            }

            Matcher committeeChamberMatcher = committeeChamberPattern.matcher(line);
            if( committeeChamberMatcher.matches()){
                String assemblyString = committeeChamberMatcher.group(1);
                return Chamber.fromString(assemblyString);
            }
        }
        return null;
    }

    private VoteLists parseVoteLists(List<String> lines){
        VoteLists voteLists = new VoteLists();
        for(String line : lines) {
            Matcher voteLineMatcher = voteLinePattern.matcher(line);
            if (voteLineMatcher.matches()) {
                log.debug("VOTE LINE: " + line);
                List<String> chunks = splitVotingLine(line);
                log.debug("  CHUNKS:  " + chunks);
                List<VoteRecord> records = Lists.map(chunks, this::parseVoteRecordChunk);
                log.debug("  RECORDS:  " + records);
                voteLists.addVoteRecords(records);
            }
        }
        return voteLists;
    }


    public BillVotes parseFileContents(String url, String content){
        String[] lines = content.split("\n");
        List<String> usefulLines = new ArrayList<>();
        for(int idx=0; idx<lines.length; idx++) {
            String line = lines[idx];

            boolean ignore = false;
            for (String ignoreLine : ignoreLines) {
                if (line.contains(ignoreLine)) {
                    ignore = true;
                }
            }
            if (!ignore) {
                usefulLines.add(line);
            }
        }

        BillIdentity billIdentity = parseBillIdentity(usefulLines);
        BillWebData billWebData = new BillWebData(url, content);
        ExpectedVoteCounts expectedVoteCounts = parseExpectedVoteCounts(usefulLines);
        Chamber votingChamber = parseVotingChamber(usefulLines);
        VoteLists voteLists = parseVoteLists(usefulLines);

        return new BillVotes(billIdentity, billWebData, expectedVoteCounts, voteLists, votingChamber);
    }

}
