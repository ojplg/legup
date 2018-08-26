package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.util.Tuple;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BillHtmlParser {

    private final String url;
    private final Document document;
    private final Chamber chamber;
    private final Long number;

    public BillHtmlParser(String url){
        try {
            this.url = url;
            this.document = Jsoup.connect(url).get();
            Tuple<Chamber, Long> tuple = parseBillIdentity();
            chamber = tuple.getFirst();
            number = tuple.getSecond();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public String getUrl(){
        return url;
    }

    public Bill getBill(){
        Bill bill = new Bill();

        bill.setChamber(chamber);
        bill.setNumber(number);
        bill.setSession(getSession());
        bill.setShortDescription(getShortDescription());

        return bill;
    }

    public long getChecksum(){
        return document.hashCode();
    }

    public long getSession(){
        // FIXME!!!
        return 100;
    }

    private Tuple<Chamber, Long> parseBillIdentity(){

        String regex = "Bill Status of ([H|S]B)(\\d+)";
        Pattern pattern = Pattern.compile(regex);

        Elements spans = document.select("span").attr("class", "heading");
        for( Element span : spans ){
            String content = span.text();
            Matcher matcher = pattern.matcher(content);
            if( matcher.matches() ){
                String chamberString = matcher.group(1);
                String numberString = matcher.group(2);
                Chamber chamber;
                switch (chamberString){
                    case "HB" :
                        chamber = Chamber.House;
                        break;
                    case "SB" :
                        chamber = Chamber.Senate;
                        break;
                    default:
                        throw new RuntimeException("How did this happen " + chamberString);
                }
                Long number = Long.parseLong(numberString);
                return new Tuple<>(chamber, number);
            }
        }
        throw new RuntimeException("Could not determine chamber and number");
    }

    public long getNumber(){
        return number;
    }

    public Chamber getChamber(){
        return chamber;
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

    public List<Tuple<String, String>> getSponsorNames(Chamber chamber){
        List<Tuple<String, String>> tuples = new ArrayList<>();

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
                String href = element.attr("href");
                Matcher matcher = MemberHtmlParser.memberIdExtractionPattern.matcher(href);
                if( matcher.matches()) {
                    String memberId = matcher.group(1);
                    Tuple<String, String> tuple = new Tuple<>(
                            text.replace("Rep. ", ""),
                            memberId);
                    tuples.add(tuple);
                }
            }
            if (started && found && "br".equals(nodeName)){
                break;
            }
        }

        return tuples;
    }
}
