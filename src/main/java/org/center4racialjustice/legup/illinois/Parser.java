package org.center4racialjustice.legup.illinois;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    public static String readFileToString(String filename)  {
        PDDocument doc = new PDDocument();
        InputStream fileStream = null;
        String content;
        try
        {
            doc.close();
            fileStream = doc.getClass().getResourceAsStream(filename);
            doc = PDDocument.load(fileStream);

            PDFTextStripper stripper = new PDFTextStripper();
            content = stripper.getText(doc);
            doc.close();
            fileStream.close();

        }
        catch (IOException ioe){
            throw new RuntimeException(ioe);
        }
        finally
        {
//            try {
//                if (fileStream != null) {
//                    System.out.println("Closing stream");
//                    fileStream.close();
//                }
//                if (doc != null) {
//                    System.out.println("Closing doc");
//                    doc.close();
//                }
//            } catch (IOException ioe){
//                throw new RuntimeException(ioe);
//            }
        }
        return content;
    }

    private static Pattern billNumberPattern = Pattern.compile("Senate Bill No. (\\d+)");
    private static Pattern summaryPattern =
            Pattern.compile("(\\d+)\\s+YEAS\\s+(\\d+)\\s+NAYS\\s+(\\d+)\\s+PRESENT");
    private static Pattern alternateSummaryPattern =
            Pattern.compile("YEAS NAYS PRESENT NOT VOTING(\\d+) (\\d+) (\\d+) (\\d+)");

    private static Pattern voteLinePattern =
            Pattern.compile("(NV|Y|N|P) .*");

    private static Pattern voteRecordPattern =
            Pattern.compile("(NV|Y|N|P) ([A-Za-z\\., ]+?)");

    private static Pattern voteRecordLinePattern =
            Pattern.compile("((NV|Y|N|P) ([A-Za-z\\., ]+?))+");


    public static boolean isVoteLine(String line){
        Matcher voteLineMatcher = voteLinePattern.matcher(line);
        return voteLineMatcher.matches();
    }

    public static VoteRecord parseVoteRecord(String input){
        Matcher matcher = voteRecordPattern.matcher(input);
        if ( matcher.matches() ){
            String voteCode = matcher.group(1);
            String nameString = matcher.group(2);
            Vote vote = Vote.fromCode(voteCode);
            Name name = Name.fromAnyString(nameString);
            return new VoteRecord(name, vote);
        }
        throw new RuntimeException("Not a vote record: " + input);
    }

    public static enum ParseState {
        InName,PossibleVote,PossibleNV;
    }

    public static List<VoteRecord> parseVoteRecordLine(String input){
        List<VoteRecord> records = new ArrayList<>();
        StringBuilder buf = new StringBuilder();
        char last = 0;
        ParseState state = null;
        Vote vote = null;

        char[] chars = input.toCharArray();
        for( char c : chars ){
            switch (c) {
                case 'N':
                    if( vote != null ){
                        Name name = Name.fromAnyString(buf.toString());
                        VoteRecord record = new VoteRecord(name, vote);
                        records.add(record);
                        vote = null;
                        buf = new StringBuilder();
                    }
                    state = ParseState.PossibleVote;
                    last = c;
                    break;
                case 'Y':
                    if( vote != null ){
                        Name name = Name.fromAnyString(buf.toString());
                        VoteRecord record = new VoteRecord(name, vote);
                        records.add(record);
                        vote = null;
                        buf = new StringBuilder();
                    }
                    state = ParseState.PossibleVote;
                    last = c;
                    break;
                case 'P':
                    if( vote != null ){
                        Name name = Name.fromAnyString(buf.toString());
                        VoteRecord record = new VoteRecord(name, vote);
                        records.add(record);
                        vote = null;
                        buf = new StringBuilder();
                    }
                    state = ParseState.PossibleVote;
                    last = c;
                    break;
                case 'V':
                    if( vote != null ){
                        Name name = Name.fromAnyString(buf.toString());
                        VoteRecord record = new VoteRecord(name, vote);
                        records.add(record);
                        vote = null;
                        buf = new StringBuilder();
                    }
                    if ( last == 'N' && state == ParseState.PossibleVote ){
                        state=ParseState.PossibleNV;
                    }
                    last = c;
                    break;
                case ' ':
                    if( state == ParseState.PossibleNV ) {
                        vote = Vote.NotVoting;
                        state = ParseState.InName;
                    }
                    if ( state == ParseState.PossibleVote ){
                        vote = Vote.fromCode(Character.toString(last));
                        state = ParseState.InName;
                    }
                    if ( state == ParseState.InName ){
                        buf.append(' ');
                    }
                    break;
                default :
                    buf.append(c);
                    break;
            }
        }

        Name name = Name.fromAnyString(buf.toString());
        VoteRecord lastRecord = new VoteRecord(name, vote);
        records.add(lastRecord);

        return records;
    }

    public static BillVotes parseFile(String filename) {
        String content = readFileToString(filename);
        String[] lines = content.split("\n");
        BillVotes bv = new BillVotes();
        bv.content = content;
        for(int idx=0; idx<lines.length; idx++){
            String line = lines[idx];

            Matcher billNumberMatcher = billNumberPattern.matcher(line);
            while( billNumberMatcher.find() ){
                String found = billNumberMatcher.group(1);
                int billNumber = Integer.parseInt(found);
                bv.billNumber = billNumber;
            }

            Matcher summaryMatcher = summaryPattern.matcher(line);
            while (summaryMatcher.find()){
                String yeas = summaryMatcher.group(1);
                bv.expectedYeas = Integer.parseInt(yeas);
                String nays = summaryMatcher.group(2);
                bv.expectedNays = Integer.parseInt(nays);
                String presents = summaryMatcher.group(3);
                bv.expectedPresent = Integer.parseInt(presents);
            }

            Matcher alternateSummaryMatcher = alternateSummaryPattern.matcher(line);
            while(alternateSummaryMatcher.find()){
                String yeas = alternateSummaryMatcher.group(1);
                bv.expectedYeas = Integer.parseInt(yeas);
                String nays = alternateSummaryMatcher.group(2);
                bv.expectedNays = Integer.parseInt(nays);
                String presents = alternateSummaryMatcher.group(3);
                bv.expectedPresent = Integer.parseInt(presents);
                String notVotings = alternateSummaryMatcher.group(4);
                bv.expectedNotVoting = Integer.parseInt(notVotings);
            }

            Matcher voteLineMatcher = voteLinePattern.matcher(line);
            while( voteLineMatcher.find() ){
//                System.out.println("VOTE LINE !!! " + line);
//                System.out.println("COUNT " + voteLineMatcher.groupCount());
//                System.out.println(voteLineMatcher.group(1));
//                System.out.println(voteLineMatcher.group(2));
//                System.out.println(voteLineMatcher.group(3));
            }
        }

        return bv;
    }
}
