import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    public static String readFileToString(String filename)  {
        PDDocument doc = new PDDocument();
        try
        {
            InputStream fileStream = doc.getClass().getResourceAsStream(filename);
            if ( fileStream == null ){
                System.out.println("No such file");
            }
            doc = PDDocument.load(fileStream);

            PDFTextStripper stripper = new PDFTextStripper();
            String content = stripper.getText(doc);

            return content;
        }
        catch (IOException ioe){
            throw new RuntimeException(ioe);
        }
        finally
        {
            if( doc != null )
            {
                try {
                    doc.close();
                } catch (IOException ioe){
                    throw new RuntimeException(ioe);
                }
            }
        }
    }

    private static Pattern billNumberPattern = Pattern.compile("Senate Bill No. (\\d+)");
    private static Pattern summaryPattern =
            Pattern.compile("(\\d+)\\s+YEAS\\s+(\\d+)\\s+NAYS\\s+(\\d+)\\s+PRESENT");
    private static Pattern alternateSummaryPattern =
            Pattern.compile("YEAS NAYS PRESENT NOT VOTING(\\d+) (\\d+) (\\d+) (\\d+)");

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
        }

        return bv;
    }
}
