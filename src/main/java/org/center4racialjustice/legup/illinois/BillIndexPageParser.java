package org.center4racialjustice.legup.illinois;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.util.Tuple;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BillIndexPageParser {

    private static final Logger log = LogManager.getLogger(BillIndexPageParser.class);

    public static String IllinoisLegislationHome = "http://www.ilga.gov/legislation/";
    public static String LegislationIndexPath = "default.asp";

    private static Pattern RangeLinkTextPattern = Pattern.compile("(\\d+) - (\\d+)");

    private final Document document;
    private final String prefix;

    public BillIndexPageParser() {
        try {
            String url = IllinoisLegislationHome + LegislationIndexPath;
            log.info("Searching " + url);
            Connection votesPageConnection = Jsoup.connect(url);
            document = votesPageConnection.get();
            prefix = IllinoisLegislationHome;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public BillIndexPageParser(InputStream inputStream, String url){
        try {
            document = Jsoup.parse(inputStream, null, url);
            prefix = "";
        } catch (IOException ex){
            throw new RuntimeException(ex);
        }
    }

    public String findSubIndexUrl(Chamber chamber, Long number){
        Elements tables = document.select("table");
        Element table = tables.get(3);
        Elements subTables = table.select("table");

        int subTableIndex = Chamber.Senate.equals(chamber) ? 1 : 2;

        Element chamberTable = subTables.get(subTableIndex);
        Elements tds = chamberTable.select("td");

        for( Element td : tds ){
            Element anchor = td.selectFirst("a");
            String url = checkAnchor(anchor, number);
            if ( url != null ){
                return url;
            }
        }

        return null;
    }

    private String checkAnchor(Element anchor, Long number){
        if ( anchor == null ){
            return null;
        }
        String linkText = anchor.text();
        Tuple<Long, Long> bounds = extractBounds(linkText);
        if ( bounds == null ){
            return null;
        }
        if( bounds.getFirst() <= number && bounds.getSecond() >= number ){
            return prefix + anchor.attr("href");
        }
        return null;
    }

    private Tuple<Long, Long> extractBounds(String anchorText){
        Matcher matcher = RangeLinkTextPattern.matcher(anchorText);
        if ( matcher.matches() ){
            Long begin = Long.parseLong(matcher.group(1));
            Long end = Long.parseLong(matcher.group(2));
            return new Tuple<>(begin, end);
        }
        return null;
    }

}
