package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Assembly;
import org.center4racialjustice.legup.domain.Legislator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MemberHtmlParser {

    private final Document document;

    private MemberHtmlParser(Document document){
        this.document = document;
    }

    public static MemberHtmlParser load(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            return new MemberHtmlParser(doc);
        } catch (IOException ex){
            throw new RuntimeException(ex);
        }

    }

    public List<Legislator> getNames(){
        Elements tables = document.select("table");
        Element table = tables.get(4);

        Elements rows = table.select("tr");

        List<Legislator> members = new ArrayList<>();

        for(Element row : rows){
            //System.out.println(" ** ROW ** ");
            Elements cells = row.select("td");
            Element firstCell = cells.first();
            Element anchor = firstCell.selectFirst("a");
            if ( anchor != null ){
                String href = anchor.attr("href");
                if( href.contains("MemberID=")){
                    String nameString = anchor.text();
                    Name name = Name.fromRegularOrderString(nameString);
                    Element disctrictCell = cells.get(3);
                    Element partyCell = cells.get(4);
                    String districtString = disctrictCell.text();
                    int district = Integer.parseInt(districtString);
                    String partyCode = partyCell.text();

                    Legislator leg = new Legislator(
                            name, partyCode, Assembly.House, district, 100
                    );
                    members.add(leg);
                }

            }
        }
        return members;
    }

}
