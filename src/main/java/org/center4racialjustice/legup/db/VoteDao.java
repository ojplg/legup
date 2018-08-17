package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.domain.Vote;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

public class VoteDao extends OneTableDao<Vote>  {

    private static final String table = "VOTES";

    private static final List<Column> columnList =
            Arrays.asList(
                    new Column<>("ID", ColumnType.Long, Vote::getId, Vote::setId),
                    new Column<>("LEGISLATOR_ID", ColumnType.Long, Vote::getLegislatorId, Vote::setLegislatorId),
                    new Column<>("BILL_ID", ColumnType.Long, Vote::getBillId, Vote::setBillId),
                    new Column<>("VOTE_SIDE", ColumnType.String, Vote::getVoteSide, Vote::setVoteSide)
            );

    public VoteDao(Connection connection) {
        super(connection, () -> new Vote(), table, columnList);
    }

}
