package org.center4racialjustice.legup.web.handlers;

import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.NameParser;
import org.center4racialjustice.legup.illinois.MemberHtmlParser;
import org.center4racialjustice.legup.service.LegislatorPersistence;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSession;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;

import java.util.List;

public class ViewParsedLegislators implements Responder {

    private final LegislatorPersistence legislatorPersistence;
    private final NameParser nameParser;

    public ViewParsedLegislators(ConnectionPool connectionPool, NameParser nameParser) {
        this.legislatorPersistence = new LegislatorPersistence(connectionPool);
        this.nameParser = nameParser;
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        String memberUrl = submission.getParameter("url");

        MemberHtmlParser parser = MemberHtmlParser.load(memberUrl, nameParser);
        List<Legislator> legislators = parser.getLegislators();
        List<Legislator> unknownLegislators = legislatorPersistence.filterOutSavedLegislators(legislators);

        String oneTimeKey = submission.setObject(LegupSession.UnknownLegislatorsKey, unknownLegislators);

        LegupResponse response = new LegupResponse(this.getClass());
        response.putVelocityData("legislators", unknownLegislators);
        response.putVelocityData("parsedLegislatorCount", legislators.size());
        response.putVelocityData("oneTimeKey", oneTimeKey);

        return response;
    }
}
