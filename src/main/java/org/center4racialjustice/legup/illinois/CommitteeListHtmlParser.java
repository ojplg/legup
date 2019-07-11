package org.center4racialjustice.legup.illinois;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.center4racialjustice.legup.util.Triple;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommitteeListHtmlParser {

    private final static Pattern LinkPattern = Pattern.compile("http://www.ilga.gov/house/committees/members.asp?CommitteeID=(\\d+)&GA=\\d+");

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

    public static CommitteeListHtmlParser load(String url) {
        CloseableHttpResponse httpResponse = null;
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();

            Document doc = Jsoup.parse(httpEntity.getContent(), "windows-1252", url);
            return new CommitteeListHtmlParser(doc);
        } catch (IOException ex){
            throw new RuntimeException(ex);
        } finally {
            try {
                if (httpResponse != null) {
                    httpResponse.close();
                }
            } catch (IOException ex){
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * Returns a triplet with Name, Code, and Link
     *
     * @return
     */
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

    public static Long getCommitteeId(Triple<String, String, String> triplet){
        String link = triplet.getThird();
        Matcher matcher = LinkPattern.matcher(link);
        if( matcher.matches() ){
            String captured = matcher.group(1);
            return Long.parseLong(captured);
        }
        return null;
    }
}
