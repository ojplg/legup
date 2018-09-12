package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.domain.NameParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MemberHtmlParser {

    private static final String AssemblyRegex = "Current (House|Senate) Members";
    private static final Pattern AssemblyPattern = Pattern.compile(AssemblyRegex);

    private static final String SessionRegex = "(\\d+)\\w\\w General Assembly";
    private static final Pattern SessionPattern = Pattern.compile(SessionRegex);

    private static final String MemberIdExtractionRegex = ".*MemberID=(\\d+).*";
    public static final Pattern MemberIdExtractionPattern = Pattern.compile(MemberIdExtractionRegex);

    private final Document document;
    private final NameParser nameParser = new NameParser(new HashMap<>());

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

    public static MemberHtmlParser loadFromInputStream(InputStream inputStream, String url){
        try {
            Document doc = Jsoup.parse(inputStream, null, url);
            return new MemberHtmlParser(doc);
        } catch (IOException ex){
            throw new RuntimeException(ex);
        }
    }

    public Chamber getAssembly(){
        Elements spans = document.select("span");
        for(Element span : spans){
            String content = span.text();
            Matcher matcher = AssemblyPattern.matcher(content);
            if ( matcher.matches() ){
                String assemblyString = matcher.group(1);
                return Chamber.fromString(assemblyString);
            }
        }
        return null;
    }

    public Long getSessionNumber(){
        Elements spans = document.select("span");
        for(Element span : spans){
            String content = span.text();
            Matcher matcher = SessionPattern.matcher(content);
            if ( matcher.matches() ){
                String sessionString = matcher.group(1);
                return Long.parseLong(sessionString);
            }
        }
        return null;
    }

    public List<Legislator> getLegislators(){
        Chamber chamber = getAssembly();
        Long sessionNumber = getSessionNumber();

        List<Legislator> legislators = new ArrayList<>();
        // main list
        legislators.addAll(readTable(4, chamber, sessionNumber));
        // former members
        legislators.addAll(readTable(5, chamber, sessionNumber));
        return legislators;
    }

    public List<Legislator> readTable(int number, Chamber chamber, Long sessionNumber){
        Elements tables = document.select("table");
        Element table = tables.get(number);

        Elements rows = table.select("tr");

        List<Legislator> members = new ArrayList<>();

        int rowCount = 0;
        for(Element row : rows){
            rowCount++;
            Elements cells = row.select("td");
            Element firstCell = cells.first();
            Element anchor = firstCell.selectFirst("a");
            if ( anchor != null ){
                String href = anchor.attr("href");
                Matcher matcher = MemberIdExtractionPattern.matcher(href);
                if( matcher.matches() ){
                    String memberId = matcher.group(1);

                    String nameString = anchor.text();
                    Name name = nameParser.fromRegularOrderString(nameString);
                    Element districtCell = cells.get(3);
                    Element partyCell = cells.get(4);
                    String districtString = districtCell.text();
                    int district = Integer.parseInt(districtString);
                    String partyCode = partyCell.text();
                    Legislator leg = new Legislator();
                    leg.setName(name);
                    leg.setChamber(chamber);
                    leg.setDistrict(district);
                    leg.setParty(partyCode);
                    leg.setSessionNumber(sessionNumber);

                    leg.setMemberId(memberId);

                    members.add(leg);
                }

            }
        }
        System.out.println("ROW COUNT " + rowCount);
        return members;
    }
}
