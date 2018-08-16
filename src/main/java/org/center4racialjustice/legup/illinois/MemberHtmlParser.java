package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Assembly;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.domain.NameParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MemberHtmlParser {

    private final Document document;
    private final NameParser nameParser = new NameParser(new HashMap<>());

    private MemberHtmlParser(Document document){
        this.document = document;
    }

    private String assemblyRegex = "Current (House|Senate) Members";
    private Pattern assemblyPattern = Pattern.compile(assemblyRegex);

    private String sessionRegex = "(\\d+)\\w\\w General Assembly";
    private Pattern sessionPattern = Pattern.compile(sessionRegex);

    public static MemberHtmlParser load(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            return new MemberHtmlParser(doc);
        } catch (IOException ex){
            throw new RuntimeException(ex);
        }

    }

    public Assembly getAssembly(){
        Elements spans = document.select("span");
        for(Element span : spans){
            String content = span.text();
            Matcher matcher = assemblyPattern.matcher(content);
            if ( matcher.matches() ){
                String assemblyString = matcher.group(1);
                return Assembly.fromString(assemblyString);
            }
        }
        return null;
    }

    public Long getSessionNumber(){
        Elements spans = document.select("span");
        for(Element span : spans){
            String content = span.text();
            Matcher matcher = sessionPattern.matcher(content);
            if ( matcher.matches() ){
                String sessionString = matcher.group(1);
                return Long.parseLong(sessionString);
            }
        }
        return null;
    }

    public List<Legislator> getLegislators(){
        Assembly assembly = getAssembly();
        Long sessionNumber = getSessionNumber();

        Elements tables = document.select("table");
        Element table = tables.get(4);

        Elements rows = table.select("tr");

        List<Legislator> members = new ArrayList<>();


        for(Element row : rows){
            Elements cells = row.select("td");
            Element firstCell = cells.first();
            Element anchor = firstCell.selectFirst("a");
            if ( anchor != null ){
                String href = anchor.attr("href");
                if( href.contains("MemberID=")){
                    String nameString = anchor.text();
                    Name name = nameParser.fromRegularOrderString(nameString);
                    Element disctrictCell = cells.get(3);
                    Element partyCell = cells.get(4);
                    String districtString = disctrictCell.text();
                    int district = Integer.parseInt(districtString);
                    String partyCode = partyCell.text();
                    Legislator leg = new Legislator();
                    leg.setFirstName(name.getFirstName());
                    leg.setMiddleInitialOrName(name.getMiddleInitial());
                    leg.setLastName(name.getLastName());
                    leg.setSuffix(name.getSuffix());
                    leg.setAssembly(assembly);
                    leg.setDistrict(district);
                    leg.setParty(partyCode);
                    leg.setSessionNumber(sessionNumber);

                    members.add(leg);
                }

            }
        }
        return members;
    }

}
