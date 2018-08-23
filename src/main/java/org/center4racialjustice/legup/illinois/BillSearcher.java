package org.center4racialjustice.legup.illinois;

import org.apache.http.client.utils.URIBuilder;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.util.Tuple;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillSearcher {

    public static String IllinoisLegislationHome = "http://www.ilga.gov";

    public static String LegislationIndexPageUrl = "http://www.ilga.gov/legislation/default.asp";

    public String gaParameterValue = "100";
    public String sessionIdParameterValue = "91";

//    private Document legislationIndexPageContents;

    public BillSearcher() {
//        try {
//            Connection connection = Jsoup.connect(LegislationIndexPageUrl);
//            legislationIndexPageContents = connection.get();
//        } catch (IOException ex){
//            throw new RuntimeException(ex);
//        }
    }

    public Map<String, String> searchForVotesUrls(String votesPageUrl){
        try {
            Connection votesPageConnection = Jsoup.connect(votesPageUrl);
            Document votesPageDocument = votesPageConnection.get();

            Elements tables = votesPageDocument.select("table");
            Element voteLinkTable = tables.get(6);

            Elements rows = voteLinkTable.select("tr");

            Map<String, String> urls = new HashMap<>();
            for( int idx=1; idx<rows.size(); idx++ ){
                Element row = rows.get(idx);
                Element td = row.selectFirst("td");
                Element anchor = td.selectFirst("a");

                String url = IllinoisLegislationHome + anchor.attr("href");
                String text = anchor.text();

                urls.put(text, url);
            }

            return urls;
        } catch (IOException ex){
            throw new RuntimeException(ex);
        }
    }


    public String convertToVotesPage(String billHomePage){
        return billHomePage.replace("/BillStatus.asp?","/votehistory.asp?");
    }

    public String searchForBaseUrl(Chamber chamber, Long number){
        try {
            String indexUrl = searchForSubIndexPage(chamber, number);
            Connection indexConnection = Jsoup.connect(indexUrl);
            Document indexDocument = indexConnection.get();

            Elements lists = indexDocument.select("ul");
            Element list = lists.get(0);

            Elements listItems = list.select("li");
            int idx = number.intValue() % 100 - 1;
            Element item = listItems.get(idx);

            Element anchor = item.selectFirst("a");
            String url = anchor.attr("href");

            return IllinoisLegislationHome + url;
        } catch (IOException ex){
            throw new RuntimeException(ex);
        }
    }

    private String uriParameter(Chamber chamber){
        switch (chamber.toString()){
            case "House" : return "HB";
            case "Senate" : return "SB";
        }
        throw new RuntimeException("Unupported chamber " + chamber);
    }

    public String searchForSubIndexPage(Chamber chamber, Long number){
        try {
            // FIXME: this will not work for the last group (which does not end at a multiple of 100)
            Tuple<Long, Long> bounds = getBounds(number);

            String base = "http://www.ilga.gov/legislation/grplist.asp";
            URIBuilder uriBuilder = new URIBuilder(base);

            uriBuilder.addParameter("num1", bounds.getFirst().toString());
            uriBuilder.addParameter("num2", bounds.getSecond().toString());
            uriBuilder.addParameter("DocTypeID", uriParameter(chamber));
            uriBuilder.addParameter("GA", gaParameterValue);
            uriBuilder.addParameter("SessionId", sessionIdParameterValue);

            return uriBuilder.toString();

        } catch (URISyntaxException ex){
            throw new RuntimeException(ex);
        }
    }

    private Tuple<Long, Long> getBounds(long number){
        long lowBound = 100 * (number / 100) + 1;
        long highBound = lowBound + 99;
        Tuple<Long,Long> bounds = new Tuple<>(lowBound, highBound);
        return bounds;
    }

    /*
    Senate 1-100
    http://www.ilga.gov/legislation/grplist.asp?num1=1&num2=100&DocTypeID=SB&GA=100&SessionId=91

    Senate 101-200
    http://www.ilga.gov/legislation/grplist.asp?num1=101&num2=200&DocTypeID=SB&GA=100&SessionId=91

    Senate 2201-2300
    http://www.ilga.gov/legislation/grplist.asp?num1=2201&num2=2300&DocTypeID=SB&GA=100&SessionId=91

    House
    601-700
    http://www.ilga.gov/legislation/grplist.asp?num1=601&num2=700&DocTypeID=HB&GA=100&SessionId=91

    House 5901-5949
    http://www.ilga.gov/legislation/grplist.asp?num1=5901&num2=5949&DocTypeID=HB&GA=100&SessionId=91

     */

    /*
    Senate 1
    http://www.ilga.gov/legislation/BillStatus.asp?DocNum=1&GAID=14&DocTypeID=SB&LegId=98844&SessionID=91&GA=100

    Senate 2
    http://www.ilga.gov/legislation/BillStatus.asp?DocNum=2&GAID=14&DocTypeID=SB&LegId=98845&SessionID=91&GA=100

    Senate 3
    http://www.ilga.gov/legislation/BillStatus.asp?DocNum=3&GAID=14&DocTypeID=SB&LegId=98846&SessionID=91&GA=100

    Senate 123
    http://www.ilga.gov/legislation/BillStatus.asp?DocNum=123&GAID=14&DocTypeID=SB&LegId=100000&SessionID=91&GA=100

    Senate 1710
    http://www.ilga.gov/legislation/BillStatus.asp?DocNum=1710&GAID=14&DocTypeID=SB&LegId=104566&SessionID=91&GA=100

    House 1109
    http://www.ilga.gov/legislation/BillStatus.asp?DocNum=1109&GAID=14&DocTypeID=HB&LegId=101586&SessionID=91&GA=100

     */
}
