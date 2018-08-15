package org.center4racialjustice.legup.illinois;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;

public class TestJsoup {

    @Test
    public void testParsingHouseMembers() throws IOException {
        Document doc = Jsoup.connect("http://www.ilga.gov/house/default.asp").get();

        Elements tables = doc.select("table");
        Element table = tables.get(4);

        Elements rows = table.select("tr");

        for(Element row : rows){
            System.out.println(" ** ROW ** ");
            System.out.println(row);
        }

    }

}
