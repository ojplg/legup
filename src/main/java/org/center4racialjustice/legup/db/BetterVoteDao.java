package org.center4racialjustice.legup.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.domain.BetterVote;
import org.center4racialjustice.legup.domain.Identifiable;
import org.center4racialjustice.legup.domain.VoteSideConverter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class BetterVoteDao {

    private static final Logger log = LogManager.getLogger(BetterVoteDao.class);

    private final String table = "VOTES";

    private final List<TypedColumn<BetterVote>> dataColumns =
            Arrays.asList(
                    new LongColumn<>("ID", "a", BetterVote::getId, BetterVote::setId),
                    new CodedEnumColumn<>("VOTE_SIDE", "a", BetterVote::getVoteSide, BetterVote::setVoteSide, new VoteSideConverter())
            );

    private final List<JoinColumn<BetterVote, ?>> joinColumns =
            Arrays.asList(
                    new JoinColumn<>("BILL_ID", "b", "bills", BetterVote::getBill, BetterVote::setBill,
                            BillDao.supplier, BillDao.typedColumnList ),
                    new JoinColumn<>("LEGISLATOR_ID", "c", "legislators", BetterVote::getLegislator, BetterVote::setLegislator,
                            LegislatorDao.supplier, LegislatorDao.typedColumnList )
            );

    private final Connection connection;

    public final Supplier<BetterVote> supplier = () -> new BetterVote();

    public BetterVoteDao(Connection connection) {
        this.connection = connection;
    }

    public long insert(BetterVote vote){
        return doInsert(connection, table, dataColumns, joinColumns, vote);
    }

    private static <T extends Identifiable> Long doInsert(Connection connection, String table, List<TypedColumn<T>> columnList, List<JoinColumn<T,?>> joinColumns, T item){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String sql = insertStatement(table, columnList, joinColumns);

        try {
            preparedStatement = connection.prepareStatement(sql);

            int index = 1;
            for ( ; index < columnList.size(); index++) {
                TypedColumn column = columnList.get(index);
                column.setValue(item, index, preparedStatement);
            }

            for( JoinColumn joinColumn : joinColumns ){
                joinColumn.setValue(item, index, preparedStatement);
                index++;
            }

            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getLong("id");
        } catch (SQLException se){
            throw new RuntimeException("Wrapped SQL exception for " + sql, se);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException se){
                log.error("Error during close",se);
            }
        }
    }

    public static <T extends Identifiable> String updateStatement(T item, String table, List<TypedColumn<T>> allColumns){
        StringBuilder buf = new StringBuilder();
        buf.append("update ");
        buf.append(table);
        buf.append(" set ");
        for(int idx=0; idx< allColumns.size(); idx++ ){
            TypedColumn column = allColumns.get(idx);
            buf.append(column.getName());
            buf.append(" = ? ");
            if (idx < allColumns.size() - 1){
                buf.append(", ");
            }
        }
        buf.append(" where id = ");
        buf.append(item.getId());
        buf.append(" RETURNING ID");

        return buf.toString();
    }


    public static <T> String insertStatement(String table, List<TypedColumn<T>> columnList, List<JoinColumn<T,?>> joinColumns){
        StringBuilder bldr = new StringBuilder();
        bldr.append("insert into ");
        bldr.append(table);
        bldr.append(" ( ");
        bldr.append(DaoHelper.typedColumnsAsString("",columnList, false));
        bldr.append(", ");
        bldr.append(DaoHelper.typedColumnsAsString("", joinColumns, false));
        bldr.append(" ) values ( DEFAULT, ");
        for(int idx=0; idx<columnList.size()+joinColumns.size()-2; idx++){
            bldr.append("?, ");
        }
        bldr.append("? ");
        bldr.append(" ) ");
        bldr.append(" RETURNING ID ");

        return bldr.toString();
    }

    public static <T> String joinSelectSql(String table, List<TypedColumn<T>> dataColumns, List<JoinColumn<T,?>> joinColumns){
        StringBuilder buf = new StringBuilder();
        buf.append("select ");
        buf.append(DaoHelper.typedColumnsAsString("a", dataColumns, true));
        for(JoinColumn joinColumn : joinColumns) {
            buf.append(", ");
            buf.append(DaoHelper.typedColumnsAsString(
                    joinColumn.getPrefix(),
                    joinColumn.getColumnList(),
                    true
            ));
        }
        buf.append(" from ");
        buf.append(table);
        buf.append(" a");
        for(JoinColumn joinColumn : joinColumns) {
            buf.append(", ");
            buf.append(joinColumn.getTable());
            buf.append(" ");
            buf.append(joinColumn.getPrefix());
        }
        buf.append(" where ");
        for( int idx=0; idx<joinColumns.size(); idx++ ){
            JoinColumn joinColumn = joinColumns.get(idx);
            if( idx > 0 ){
                buf.append(" and ");
            }
            buf.append("a.");
            buf.append(joinColumn.getName());
            buf.append("=");
            buf.append(joinColumn.getPrefix());
            buf.append(".id");
        }

        return buf.toString();
    }

    public BetterVote read(long id){

        StringBuilder buf = new StringBuilder();

        buf.append(joinSelectSql(table, dataColumns, joinColumns));

        buf.append(" and a.id = ");
        buf.append(id);

        String sql = buf.toString();

        System.out.println("SQL ");
        System.out.println(sql);

        Statement statement = null;
        ResultSet resultSet = null;

        List<BetterVote> votes = new ArrayList<>();

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            while(resultSet.next()){
                BetterVote vote = supplier.get();
                for(TypedColumn column : dataColumns){
                    column.populate(vote, resultSet);
                }

                for(JoinColumn joinColumn : joinColumns ){
                    joinColumn.populate(vote, resultSet);
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
