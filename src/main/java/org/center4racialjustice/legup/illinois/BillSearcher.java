package org.center4racialjustice.legup.illinois;

import java.util.Map;

public class BillSearcher {

    public Map<String, String> searchForVotesUrls(String votesPageUrl){
        BillVotesListParser billVotesListParser = new BillVotesListParser(votesPageUrl);
        return billVotesListParser.grabAllVotesUrls();
    }

    public String convertToVotesPage(String billHomePage){
        return billHomePage.replace("/BillStatus.asp?","/votehistory.asp?");
    }

    public String searchForBaseUrl(LegislationType legislationType, Long number){
        String indexUrl = subIndexPageUrl(legislationType, number);
        BillSubIndexPageParser parser = new BillSubIndexPageParser(indexUrl);
        return parser.parseOutBillUrl(number.intValue());
    }

    public String subIndexPageUrl(LegislationType legislationType, Long number) {
        BillIndexPageParser billIndexPageParser = new BillIndexPageParser();
        return billIndexPageParser.findSubIndexUrl(legislationType, number);
    }
}
