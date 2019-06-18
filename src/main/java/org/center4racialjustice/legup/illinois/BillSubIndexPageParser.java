package org.center4racialjustice.legup.illinois;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;

public class BillSubIndexPageParser {

    private static final Logger log = LogManager.getLogger(BillSubIndexPageParser.class);

    public static String IllinoisLegislationHome = "http://www.ilga.gov";

    private final Document document;
    private final String prefix;

    public BillSubIndexPageParser(String url) {
        try {
            log.info("Searching " + url);
            Connection votesPageConnection = Jsoup.connect(url);
            document = votesPageConnection.get();
            prefix = IllinoisLegislationHome;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public BillSubIndexPageParser(InputStream inputStream, String url){
        try {
            document = Jsoup.parse(inputStream, null, url);
            prefix = "";
        } catch (IOException ex){
            throw new RuntimeException(ex);
        }
    }

    public String parseOutBillUrl(int number){

        log.info("Searching for number " + number);

        Elements lists = document.select("ul");
        Element list = lists.get(0);

        Elements listItems = list.select("li");

        int idx;
        if( number % 100 == 0 ){
            idx = 99;
        } else {
            idx = number % 100 - 1;
        }
        Element item = listItems.get(idx);

        Element anchor = item.selectFirst("a");
        String billUrl = prefix + anchor.attr("href");

        return  billUrl;
    }

}
