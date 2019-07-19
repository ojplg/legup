package org.center4racialjustice.legup.illinois;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.domain.Chamber;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        return parseVotesTable(voteLinkTable, true);
    }

    public Map<String, String> grabVotesUrls(){
        Elements tables = document.select("table");
        Element voteLinkTable = tables.get(6);
        return parseVotesTable(voteLinkTable, false);
    }

    public Map<String, String> grabAllVotesUrls(){
        Map<String,String> map = grabVotesUrls();
        map.putAll(grabCommitteeVotesUrls());
        return map;
    }

    private Map<String, String> parseVotesTable(Element voteLinkTable, boolean committee){
        Map<String,String> urls = new HashMap<>();

        List<VoteLinkInfo> linkInfos = parseTable(voteLinkTable, committee);
        for (VoteLinkInfo linkInfo : linkInfos) {
            String key = linkInfo.getCode() + "."
                    + linkInfo.getVoteDescription() + "."
                    + VoteLinkInfo.ShortDateFormatter.format(linkInfo.getVoteDate());
            urls.put(key, linkInfo.getPdfUrl());
        }

        return urls;
    }

    private List<VoteLinkInfo> parseTable(Element voteLinkTable, boolean committee){
        List<VoteLinkInfo> voteLinkInfos = new ArrayList<>();

        Elements rows = voteLinkTable.select("tr");

        if( rows.size() <= 1 ){
            return voteLinkInfos;
        }

        for( int idx=1; idx<rows.size(); idx++ ){
            Element row = rows.get(idx);
            Elements cells = row.select("td");

            if( cells.size() <= 1 ){
                continue;
            }

            Element linkCell = cells.get(0);
            Element anchor = linkCell.selectFirst("a");

            if( anchor == null ){
                continue;
            }

            Element chamberCell = cells.get(1);
            String chamberText = chamberCell.text();
            Chamber chamber = Chamber.fromString(chamberText);

            String url = prefix + anchor.attr("href");
            String text = anchor.text();

            VoteLinkInfo voteLinkInfo = VoteLinkInfo.create(
                    text, chamber, committee, url);

            if( voteLinkInfo != null ){
                voteLinkInfos.add(voteLinkInfo);
            }
        }

        return voteLinkInfos;
    }

    public List<VoteLinkInfo> parseVoteLinks(){
        Elements tables = document.select("table");
        Element fullChamberVotesTable = tables.get(6);
        List<VoteLinkInfo> infos = parseTable(fullChamberVotesTable, false);
        if( tables.size() > 7 ) {
            Element committeeVotesTable = tables.get(7);
            List<VoteLinkInfo> committeeInfos = parseTable(committeeVotesTable, true);
            infos.addAll(committeeInfos);
        }
        log.info("Discovered vote links: " + infos.size());
        return infos;
    }

}
