package org.center4racialjustice.legup.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.domain.Legislator;
import org.center4racialjustice.legup.domain.Vote;
import org.center4racialjustice.legup.domain.Identifiable;
import org.center4racialjustice.legup.domain.VoteSideConverter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class VoteDao {

    private static final Logger log = LogManager.getLogger(VoteDao.class);

    private final String table = "VOTES";

    private final List<TypedColumn<Vote>> dataColumns =
            Arrays.asList(
                    new LongColumn<>("ID", "a", Vote::getId, Vote::setId),
                    new CodedEnumColumn<>("VOTE_SIDE", "a", Vote::getVoteSide, Vote::setVoteSide, new VoteSideConverter())
            );

    private final JoinColumn<Vote,Bill> billColumn =
            new JoinColumn<>("BILL_ID", "b", "bills", Vote::getBill, Vote::setBill,
                    BillDao.supplier, BillDao.typedColumnList );

    private final JoinColumn<Vote,Legislator> legislatorColumn =
            new JoinColumn<>("LEGISLATOR_ID", "c", "legislators", Vote::getLegislator, Vote::setLegislator,
                    LegislatorDao.supplier, LegislatorDao.typedColumnList );


    private final List<JoinColumn<Vote, ?>> joinColumns =
            Arrays.asList( billColumn, legislatorColumn );

    private final Connection connection;

    public final Supplier<Vote> supplier = () -> new Vote();

    public VoteDao(Connection connection) {
        this.connection = connection;
    }

    public long insert(Vote vote){
        return doInsert(connection, table, dataColumns, joinColumns, vote);
    }

    public static <T extends Identifiable> Long doInsert(Connection connection, String table, List<TypedColumn<T>> columnList, List<JoinColumn<T,?>> joinColumns, T item){
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

    public static <T> List<T> doSelect(Connection connection, String sql, Supplier<T> supplier, List<TypedColumn<T>> dataColumns, List<JoinColumn<T,?>> joinColumns){

        Statement statement = null;
        ResultSet resultSet = null;

        List<T> items = new ArrayList<>();

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            while(resultSet.next()){
                T item = supplier.get();
                for(TypedColumn column : dataColumns){
                    column.populate(item, resultSet);
                }

                for(JoinColumn joinColumn : joinColumns ){
                    joinColumn.populate(item, resultSet);
                }

                items.add(item);
            }
            return items;

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

    public List<Vote> readByBill(Bill bill){
        StringBuilder buf = new StringBuilder();

        buf.append(DaoHelper.joinSelectSql(table, dataColumns, Collections.singletonList(legislatorColumn)));

        buf.append(" and a.bill_id = ");
        buf.append(bill.getId());
        String sql = buf.toString();

        System.out.println(sql);

        List<Vote> votes = doSelect(connection, sql, supplier, dataColumns, Collections.singletonList(legislatorColumn));

        votes.forEach(v -> v.setBill(bill));

        return votes;
    }

    public Vote read(long id){

        StringBuilder buf = new StringBuilder();

        buf.append(DaoHelper.joinSelectSql(table, dataColumns, joinColumns));

        buf.append(" and a.id = ");
        buf.append(id);

        String sql = buf.toString();

        List<Vote> votes = doSelect(connection, sql, supplier, dataColumns, joinColumns);

        return DaoHelper.fromSingletonList(votes, "Reading vote");
    }

}
