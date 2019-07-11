package org.center4racialjustice.legup.web.responders;

import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.Chamber;
import org.center4racialjustice.legup.domain.Committee;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.NameParser;
import org.center4racialjustice.legup.illinois.CommitteeSearcher;
import org.center4racialjustice.legup.service.LegislatorPersistence;
import org.center4racialjustice.legup.web.HtmlLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSession;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;

import java.util.List;

public class ViewParsedCommittees implements Responder {

    private final LegislatorPersistence legislatorPersistence;
    private final NameParser nameParser;

    public ViewParsedCommittees(ConnectionPool connectionPool, NameParser nameParser) {
        this.legislatorPersistence = new LegislatorPersistence(connectionPool);
        this.nameParser = nameParser;
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        long sessionNumber = submission.getLongRequestParameter("session_number");
        String chamberString = submission.getParameter("chamber_radios");
        Chamber chamber = Chamber.fromString(chamberString);

        List<Legislator> legislators = legislatorPersistence.readLegislators(chamber, sessionNumber);

        CommitteeSearcher searcher = new CommitteeSearcher(chamber, nameParser, legislators);
        List<Committee> found = searcher.search();

        String oneTimeKey = submission.setObject(LegupSession.CommitteeDataKey, found);

        HtmlLegupResponse response = HtmlLegupResponse.simpleResponse(this.getClass(), submission.getLoggedInUser());
        response.putVelocityData("committees", found);
        response.putVelocityData("committeeCount", found.size());
        response.putVelocityData("oneTimeKey", oneTimeKey);

        return response;
    }
}
