package org.center4racialjustice.legup.illinois;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

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

    private static Pattern billNumberPattern = Pattern.compile("Senate Bill No. (\\d+)");
    private static Pattern summaryPattern =
            Pattern.compile("(\\d+)\\s+YEAS\\s+(\\d+)\\s+NAYS\\s+(\\d+)\\s+PRESENT");
    private static Pattern alternateSummaryPattern =
            Pattern.compile("YEAS NAYS PRESENT NOT VOTING(\\d+) (\\d+) (\\d+) (\\d+)");

    private static Pattern voteLinePattern =
            Pattern.compile("(NV|Y|N|P) .*");

    public static boolean isVoteLine(String line){
        Matcher voteLineMatcher = voteLinePattern.matcher(line);
        return voteLineMatcher.matches();
    }

    public static int findNextPossibleRecordIndex(String input){
        String[] markers = { "N ", "Y ", "P ", "NV "};
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
            Vote vote = Vote.fromCode(prefix);
            String end = remainder.substring(firstSpace);
            int divider = findNextPossibleRecordIndex(end);
            if( divider == -1 ){
                Name name = Name.fromLastNameFirstString(end);
                VoteRecord record = new VoteRecord(name, vote);
                remainder = "";
                records.add(record);
            } else {
                String nameString = end.substring(0, divider);
                remainder = end.substring(divider);
                Name name = Name.fromLastNameFirstString(nameString);
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

    public static BillVotes parseFileContents(String content){
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
