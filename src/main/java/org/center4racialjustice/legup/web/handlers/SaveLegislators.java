package org.center4racialjustice.legup.web.handlers;

import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.service.LegislatorPersistence;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSession;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;

import java.util.List;

public class SaveLegislators implements Responder {

    private LegislatorPersistence legislatorPersistence;

    public SaveLegislators(ConnectionPool connectionPool) {
        this.legislatorPersistence = new LegislatorPersistence(connectionPool);
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        List<Legislator> unknownLegislators = (List<Legislator>) submission.getObject(LegupSession.UnknownLegislatorsKey);

        LegupResponse response = new LegupResponse(this.getClass());

        if( unknownLegislators != null ) {
            int savedCount = legislatorPersistence.insertLegislators(unknownLegislators);
            response.putVelocityData("saved_legislator_count", savedCount);
        } else {
            // FIXME: Actually, this should do something else.
            // Display an error or something
            response.putVelocityData("saved_legislator_count", 0);
        }
        return response;
    }
}
