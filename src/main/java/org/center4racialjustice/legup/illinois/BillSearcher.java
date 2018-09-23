package org.center4racialjustice.legup.illinois;

import org.center4racialjustice.legup.domain.Chamber;

import java.util.Map;

public class BillSearcher {

    public Map<String, String> searchForVotesUrls(String votesPageUrl){
        BillVotesListParser billVotesListParser = new BillVotesListParser(votesPageUrl);
        return billVotesListParser.grabVotesUrls();
    }

    public String convertToVotesPage(String billHomePage){
        return billHomePage.replace("/BillStatus.asp?","/votehistory.asp?");
    }

    public String searchForBaseUrl(Chamber chamber, Long number){
        String indexUrl = subIndexPageUrl(chamber, number);
        BillSubIndexPageParser parser = new BillSubIndexPageParser(indexUrl);
        return parser.parseOutBillUrl(number.intValue());
    }

    public String subIndexPageUrl(Chamber chamber, Long number) {
        BillIndexPageParser billIndexPageParser = new BillIndexPageParser();
        return billIndexPageParser.findSubIndexUrl(chamber, number);
    }
}
