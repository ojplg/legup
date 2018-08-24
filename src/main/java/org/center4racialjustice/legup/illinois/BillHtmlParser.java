package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Chamber;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BillHtmlParser {

    private final Document document;

    public BillHtmlParser(String url){
        try {
            this.document = Jsoup.connect(url).get();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public String getShortDescription(){
        Elements spans = document.select("span");
        int idx = 0;
        boolean set = false;
        for( ; idx< spans.size(); idx++ ){
            Element span = spans.get(idx);
            if( span.text().equals("Short Description:")){
                set = true;
                break;
            }
        }
        if( set ){
            Element span = spans.get(idx+1);
            return span.text();
        }
        return null;
    }

    public List<String> getSponsorNames(Chamber chamber){
        List<String> names = new ArrayList<>();

        String startSpanText;
        switch (chamber.toString()) {
            case "House" :
                startSpanText = "House Sponsors";
                break;
            case "Senate":
                startSpanText = "Senate Sponsors";
                break;
            default :
                throw new RuntimeException("Unknown chamber " + chamber);
        }

        boolean started = false;
        boolean found = false;
        for ( Element element : document.getAllElements() ){
            String nodeName = element.nodeName();
            if ("span".equals(nodeName)){
                String text = element.text();
                if ( text.equals(startSpanText)){
                    started = true;
                }
            }
            if( started && "a".equals(nodeName)){
                found = true;
                String text = element.text();
                names.add(text.replace("Rep. ", ""));
            }
            if (started && found && "br".equals(nodeName)){
                break;
            }
        }

        return names;
    }
}
