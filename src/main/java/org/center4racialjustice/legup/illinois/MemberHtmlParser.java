package org.center4racialjustice.legup.illinois;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemberHtmlParser {

    private final Document document;

    private MemberHtmlParser(Document document){
        this.document = document;
    }

    public static MemberHtmlParser load(String url) {
        try {
            Document doc = Jsoup.connect("http://www.ilga.gov/house/default.asp").get();
            return new MemberHtmlParser(doc);
        } catch (IOException ex){
            throw new RuntimeException(ex);
        }

    }

    private boolean isMemberRow(Element row){
        Elements cells = row.select("td");
        Element firstCell = cells.first();
        Element anchor = firstCell.select("a").first();
        if (anchor != null){
            String href = anchor.attr("href");
            if( href.contains("MemberID=")){
                return true;
            }
        }
        return false;
    }


    public List<String> getNames(){
        Elements tables = document.select("table");
        Element table = tables.get(4);

        Elements rows = table.select("tr");

        List<String> names = new ArrayList<>();

        for(Element row : rows){
            //System.out.println(" ** ROW ** ");
            Elements cells = row.select("td");
            Element firstCell = cells.first();
            Element anchor = firstCell.selectFirst("a");
            if ( anchor != null ){
                String href = anchor.attr("href");
                if( href.contains("MemberID=")){
                    names.add(anchor.text());
                }

            }
        }
        return names;
    }

}
