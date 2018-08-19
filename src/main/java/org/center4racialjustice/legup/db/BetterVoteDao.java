package org.center4racialjustice.legup.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.domain.BetterVote;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.VoteSideConverter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BetterVoteDao {

    private static final Logger log = LogManager.getLogger(BetterVoteDao.class);

    private static final String table = "VOTES";

    private static final List<Column> columnList =
            Arrays.asList(
                    new Column<>("ID", ColumnType.Long, BetterVote::getId, BetterVote::setId),
                    new Column<>("VOTE_SIDE", ColumnType.CodedEnum, BetterVote::getVoteSide, BetterVote::setVoteSide)
            );

    private static final List<ReferenceColumn> referenceColumnList  =
            Arrays.asList(
                    new ReferenceColumn<>("BILL_ID",  "b", BillDao.table, BillDao.columnList, BillDao.supplier, BetterVote::setBill),
                    new ReferenceColumn<>("LEGISLATOR_ID", "c", LegislatorDao.table, LegislatorDao.columnList, LegislatorDao.supplier, BetterVote::setLegislator )
            );

    private final Connection connection;

    public BetterVoteDao(Connection connection) {
        this.connection = connection;
    }

    public BetterVote readOne(long id){

        StringBuilder buf = new StringBuilder();
        buf.append("select ");
        buf.append(DaoHelper.columnsAsString("a", columnList, true));
        for(ReferenceColumn referenceColumn : referenceColumnList) {
            buf.append(", ");
            buf.append(DaoHelper.columnsAsString(
                    referenceColumn.getPrefix(),
                    referenceColumn.getColumnList(),
                    true
            ));
        }
        buf.append(" from ");
        buf.append(table);
        buf.append(" a");
        for(ReferenceColumn referenceColumn : referenceColumnList) {
            buf.append(", ");
            buf.append(referenceColumn.getTable());
            buf.append(" ");
            buf.append(referenceColumn.getPrefix());
        }
        buf.append(" where a.id = ");
        buf.append(id);
        for( ColumnDescription cd : referenceColumnList ){
            buf.append(" and ");
            buf.append("a.");
            buf.append(cd.getName());
            buf.append("=");
            buf.append(cd.getPrefix());
            buf.append(".id");
        }

        String sql = buf.toString();

        Statement statement = null;
        ResultSet resultSet = null;

        List<BetterVote> votes = new ArrayList<>();

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            while(resultSet.next()){
                BetterVote vote = new BetterVote();
                vote.setId(resultSet.getLong("aid"));
                vote.setVoteSide(VoteSideConverter.INSTANCE.fromCode(resultSet.getString("avote_side")));

                for(ReferenceColumn referenceColumn : referenceColumnList ){
                    Object item = DaoHelper.populate(
                            referenceColumn.getPrefix(), resultSet, referenceColumn.getColumnList(), referenceColumn.getSupplier() );
                    referenceColumn.getSetter().accept(vote, item);
                }
                
                votes.add(vote);
            }

            return DaoHelper.fromSingletonList(votes, "Reading vote");

        } catch (SQLException se) {
            throw new RuntimeException(se);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException se){
                log.error("Error during close",se);
            }
        }



    }

}
