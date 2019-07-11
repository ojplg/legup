package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Name;
import org.center4racialjustice.legup.domain.NameParser;
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

public class CommitteeMemberParser {

    private final static Pattern MemberLinkPattern = Pattern.compile(".*\\?MemberID=(\\d+)$");
    private final Document document;
    private final NameParser nameParser;

    public CommitteeMemberParser(Document document, NameParser nameParser) {
        this.document = document;
        this.nameParser = nameParser;
    }

    public static CommitteeMemberParser loadFromInputStream(InputStream inputStream, String url, NameParser nameParser){
        try {
            Document doc = Jsoup.parse(inputStream, null, url);
            return new CommitteeMemberParser(doc, nameParser);
        } catch (IOException ex){
            throw new RuntimeException(ex);
        }
    }

    public List<Triple<String, Name, Long>> parseMembers(){
        Elements tables = document.select("table");
        Element table = tables.get(5);
        Elements rows = table.select("tr");

        List<Triple<String, Name, Long>> memberData = new ArrayList<>();

        for(int idx=1; idx<rows.size(); idx++){
            Element row = rows.get(idx);

            Elements cells = row.select("td");

            Element titleCell = cells.get(0);
            String rawTitle = titleCell.text();
            String title = rawTitle.replace(":","").trim();

            Element linkCell = cells.get(1);
            String nameString = linkCell.text();
            Name name = nameParser.fromRegularOrderString(nameString);

            Element anchor = linkCell.selectFirst("a");
            String link = anchor.attr("href");

            Matcher matcher = MemberLinkPattern.matcher(link);
            matcher.matches();
            String memberIdString = matcher.group(1);
            Long memberId = Long.parseLong(memberIdString);
            memberData.add(new Triple<>(title, name, memberId));
        }

        return memberData;
    }

}
