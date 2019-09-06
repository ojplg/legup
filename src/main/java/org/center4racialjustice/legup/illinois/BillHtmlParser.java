package org.center4racialjustice.legup.illinois;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.BillEvent;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.LegislationIdentity;
import org.center4racialjustice.legup.domain.NameParser;
import org.center4racialjustice.legup.domain.RawBillEvent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BillHtmlParser {

    private static final Logger log = LogManager.getLogger(BillHtmlParser.class);

    private static final DateTimeFormatter BILL_EVENT_DATE_FORMAT = DateTimeFormatter.ofPattern("M/d/yyyy");

    private final String url;
    private final Document document;
    private final NameParser nameParser;

    private LegislationIdentity legislationIdentity;

    public BillHtmlParser(String url, NameParser nameParser){
        CloseableHttpResponse httpResponse = null;
        try {
            this.url = url;
            log.info("Searching " + url);

            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();

            this.document = Jsoup.parse(httpEntity.getContent(), "windows-1252", url);
            this.nameParser = nameParser;

            legislationIdentity = parseBillIdentity();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            if( httpResponse != null ) {
                try {
                    httpResponse.close();
                } catch (IOException ex){
                    log.error(ex);
                }
            }
        }
    }

    public BillHtmlParser(InputStream htmlStream, String url){
        try {
            this.url = url;
            this.document = Jsoup.parse(htmlStream, "windows-1252", url);
            this.nameParser = new NameParser();
            legislationIdentity = parseBillIdentity();
        } catch (IOException ex){
            throw new RuntimeException(ex);
        }
    }

    public String getUrl(){
        return url;
    }

    public Bill getBill(){
        Bill bill = new Bill();

        bill.setLegislationIdentity(legislationIdentity);
        bill.setSession(getSession());
        bill.setShortDescription(getShortDescription());

        return bill;
    }

    public long getChecksum(){
        return document.outerHtml().hashCode();
    }

    public long getSession(){

        String regex = "(\\d\\d\\d)(?:th|st) General Assembly";
        Pattern pattern = Pattern.compile(regex);

        Elements spans = document.select("span").attr("class", "heading");
        for( Element span : spans ){
            String content = span.text();
            Matcher matcher = pattern.matcher(content);
            if( matcher.matches() ){
                String sessionString = matcher.group(1);
                return Long.parseLong(sessionString);
            }
        }
        throw new RuntimeException("Could not determine session");
    }

    private LegislationIdentity parseBillIdentity(){

        String regex = "Bill Status of ([H|S])([A-Z]+)(\\d+)";
        Pattern pattern = Pattern.compile(regex);

        Elements spans = document.select("span").attr("class", "heading");
        for( Element span : spans ){
            String content = span.text();
            Matcher matcher = pattern.matcher(content);
            if( matcher.matches() ){
                String chamberString = matcher.group(1);
                String subtypeString = matcher.group(2);
                String numberString = matcher.group(3);
                Chamber chamber;
                switch (chamberString){
                    case "H" :
                        chamber = Chamber.House;
                        break;
                    case "S" :
                        chamber = Chamber.Senate;
                        break;
                    default:
                        throw new RuntimeException("Chamber String Unrecognized " + chamberString);
                }
                String legislationSubType = LegislationType.subTypeStringFromCode(subtypeString);
                LegislationType legislationType = LegislationType.fromChamberAndSubType(chamber, legislationSubType);
                Long number = Long.parseLong(numberString);

                LegislationIdentity legislationIdentity = new LegislationIdentity();
                legislationIdentity.setLegislationType(legislationType);
                legislationIdentity.setNumber(number);
                return legislationIdentity;
            }
        }
        throw new RuntimeException("Could not determine chamber and number");
    }

    public long getNumber(){
        return legislationIdentity.getNumber();
    }

    public Chamber getChamber(){
        return legislationIdentity.getChamber();
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

    public SponsorNames getSponsorNames(){
        List<SponsorName> houseSponsors = getSponsorNames("House Sponsors");
        List<SponsorName> senateSponsors = getSponsorNames("Senate Sponsors");

        return new SponsorNames(houseSponsors, senateSponsors);
    }

    private List<SponsorName> getSponsorNames(String startSpanText){
        List<SponsorName> sponsorNames = new ArrayList<>();

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
                String href = element.attr("href");
                Matcher matcher = MemberHtmlParser.MemberIdExtractionPattern.matcher(href);
                if( matcher.matches()) {
                    String memberId = matcher.group(1);
                    SponsorName name = new SponsorName(
                            text.replace("Rep. ", "").replace("Sen.",""),
                            memberId);
                    sponsorNames.add(name);
                }
            }
            if (started && found && "br".equals(nodeName)){
                break;
            }
        }

        return sponsorNames;
    }

    public List<RawBillEvent> getRawBillEvents(){
        Elements tables = document.select("table")
                .select("[width=600]")
                .select("[cellspacing=0]")
                .select("[cellpadding=2]")
                .select("[border=1]");

        Element table = tables.last();

        Elements rows = table.select("tr").next();

        List<RawBillEvent> events = new ArrayList<>();

        for(Element row : rows){
            Elements cells = row.select("td");
            Element dateCell = cells.get(0);
            String dateString = Parser.unescapeEntities(dateCell.text(), false);
            dateString = BadHtmlCleaner.cleanDateString(dateString);

            LocalDate date = LocalDate.parse(dateString, BILL_EVENT_DATE_FORMAT);

            Element chamberCell = cells.get(1);
            String chamberString = Parser.unescapeEntities(chamberCell.text(), false);
            Chamber chamber = Chamber.fromString(chamberString);

            Element contentCell = cells.get(2);
            String contentString = contentCell.text();

            Element anchor = contentCell.selectFirst("a");
            String link = "";
            if( anchor != null ){
                link = anchor.attr("href");
            }

            RawBillEvent rawBillEvent = new RawBillEvent(date, chamber, contentString, link);
            events.add(rawBillEvent);
        }
        return events;
    }

    public List<BillEvent> getBillEvents(){

        List<RawBillEvent> rawBillEvents = getRawBillEvents();
        BillEventParser billEventParser = new BillEventParser(nameParser);

        List<BillEvent> events = new ArrayList<>();
        for(RawBillEvent rawBillEvent : rawBillEvents){
            BillEvent billEvent = billEventParser.parse(rawBillEvent);
            events.add(billEvent);
        }

        return events;
    }
}
