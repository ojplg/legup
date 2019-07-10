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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BillVotesListParser {

    private final Logger log = LogManager.getLogger(BillVotesListParser.class);

    public static String IllinoisLegislationHome = "http://www.ilga.gov";

    private final Document document;
    private final String prefix;

    public BillVotesListParser(String url) {
        try {
            log.info("Searching: " + url);
            Connection votesPageConnection = Jsoup.connect(url);
            document = votesPageConnection.get();
            prefix = IllinoisLegislationHome;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public BillVotesListParser(InputStream inputStream, String url){
        try {
            document = Jsoup.parse(inputStream, null, url);
            prefix = "";
        } catch (IOException ex){
            throw new RuntimeException(ex);
        }
    }

    public Map<String, String> grabCommitteeVotesUrls(){
        Elements tables = document.select("table");
        Element voteLinkTable = tables.get(7);
        return parseVotesTable(voteLinkTable);
    }

    public Map<String, String> grabVotesUrls(){
        Elements tables = document.select("table");
        Element voteLinkTable = tables.get(6);
        return parseVotesTable(voteLinkTable);
    }

    public Map<String, String> grabAllVotesUrls(){
        Map<String,String> map = grabVotesUrls();
        map.putAll(grabCommitteeVotesUrls());
        return map;
    }

    private Map<String, String> parseVotesTable(Element voteLinkTable){
        Elements rows = voteLinkTable.select("tr");

        Map<String, String> urls = new HashMap<>();
        for( int idx=1; idx<rows.size(); idx++ ){
            Element row = rows.get(idx);
            Element td = row.selectFirst("td");
            Element anchor = td.selectFirst("a");

            if ( anchor != null ) {
                String url = prefix + anchor.attr("href");
                String text = anchor.text();

                urls.put(text, url);
            }
        }

        for(Map.Entry<String, String> entry : urls.entrySet()){
            log.info("Found url for " + entry.getKey() + " with value " + entry.getValue());
        }

        return urls;

    }

}
