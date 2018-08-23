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

    public List<String> getSponsorNames(Chamber chamber){
        Elements anchors = document.select("a").attr("class", "content notranslate");

        String urlRegex;
        switch (chamber.toString()) {
            case "House" :
                urlRegex = ".*house/rep\\.asp.*";
                break;
            case "Senate":
                urlRegex = ".*senate/Senator\\.asp.*";
                break;
            default :
                throw new RuntimeException("Unknown chamber " + chamber);
        }

        List<String> names = new ArrayList<>();
        for (Element anchor : anchors){
            String href = anchor.attr("href");
            if ( href.matches(urlRegex)){
                String txt = anchor.text();
                names.add(txt.replace("Rep. ",""));
            }
        }
        return names;
    }
}
