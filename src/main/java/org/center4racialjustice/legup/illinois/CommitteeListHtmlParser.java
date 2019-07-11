package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.util.Triple;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CommitteeListHtmlParser {

    private final Document document;

    public CommitteeListHtmlParser(Document document) {
        this.document = document;
    }

    public static CommitteeListHtmlParser loadFromInputStream(InputStream inputStream, String url){
        try {
            Document doc = Jsoup.parse(inputStream, null, url);
            return new CommitteeListHtmlParser(doc);
        } catch (IOException ex){
            throw new RuntimeException(ex);
        }
    }

    public List<Triple<String, String, String>> parseCommitteeLinks(){

        Elements tables = document.select("table");
        Element table = tables.get(4);
        Elements rows = table.select("tr");

        List<Triple<String, String, String>> committeeData = new ArrayList<>();

        for(int idx=1; idx<rows.size(); idx++){
            Element row = rows.get(idx);
            Elements cells = row.select("td");
            Element linkCell = cells.get(0);

            String name = linkCell.text();
            Element anchor = linkCell.selectFirst("a");
            String link = anchor.attr("href");

            Element codeCell = cells.get(1);
            String code = codeCell.text();

            Triple<String, String, String> committee = new Triple<>(name, code, link);
            committeeData.add(committee);
        }

        return committeeData;
    }
}
