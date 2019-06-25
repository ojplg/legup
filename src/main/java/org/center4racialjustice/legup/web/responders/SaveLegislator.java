package org.center4racialjustice.legup.web.responders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.db.LegislatorDao;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.web.ContinueLegupResponse;
import org.center4racialjustice.legup.web.LegupResponse;
import org.center4racialjustice.legup.web.LegupSubmission;
import org.center4racialjustice.legup.web.Responder;

import java.util.Collections;

public class SaveLegislator implements Responder {

    private static final Logger log = LogManager.getLogger(SaveLegislator.class);

    private final ConnectionPool connectionPool;

    public SaveLegislator(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public LegupResponse handle(LegupSubmission submission) {
        long legislatorId = submission.getLongRequestParameter("legislator_id");
        String firstName = submission.getParameter("first_name");
        String middleInitialOrName = submission.getParameter("middle_initial_or_name");
        String lastName = submission.getParameter("last_name");
        String suffix = submission.getParameter("suffix");

        return connectionPool.runAndCommit(connection -> {
            LegislatorDao legislatorDao = new LegislatorDao(connection);
            Legislator legislator = legislatorDao.read(legislatorId);

            log.info("Updating legislator " + legislator);

            legislator.setFirstName(firstName);
            legislator.setMiddleInitialOrName(middleInitialOrName);
            legislator.setLastName(lastName);
            legislator.setSuffix(suffix);

            log.info("Updated legislator to " + legislator);

            legislatorDao.update(legislator);

            LegupResponse response = new ContinueLegupResponse(ViewLegislators.class,
                    Collections.singletonMap("session_number", String.valueOf(legislator.getSessionNumber())));
            return response;
        });
    }

}
