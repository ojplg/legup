package org.center4racialjustice.legup.db;

import org.center4racialjustice.legup.domain.Legislator;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class LegislatorDao implements Dao<Legislator> {

    public static String table = "legislators";

    public static Supplier<Legislator> supplier = () -> new Legislator();

    public static List<Column> columnList =
            Arrays.asList(
                    new Column<>("ID", ColumnType.Long, Legislator::getId, Legislator::setId),
                    new Column<>("FIRST_NAME", ColumnType.String, Legislator::getFirstName, Legislator::setFirstName),
                    new Column<>("MIDDLE_NAME_OR_INITIAL", ColumnType.String, Legislator::getMiddleInitialOrName, Legislator::setMiddleInitialOrName),
                    new Column<>("LAST_NAME", ColumnType.String, Legislator::getLastName, Legislator::setLastName),
                    new Column<>("SUFFIX", ColumnType.String, Legislator::getSuffix, Legislator::setSuffix),
                    new Column<>("CHAMBER", ColumnType.String, Legislator::getChamberString, Legislator::setChamberFromString),
                    new Column<>("DISTRICT", ColumnType.Long, Legislator::getDistrict, Legislator::setDistrict),
                    new Column<>("PARTY", ColumnType.String, Legislator::getParty, Legislator::setParty),
                    new Column<>("SESSION_NUMBER", ColumnType.Long, Legislator::getSessionNumber, Legislator::setSessionNumber)
            );

    private final Connection connection;

    public LegislatorDao(Connection connection){
        this.connection = connection;
    }

    public long save(Legislator legislator){
        return DaoHelper.save(connection, table, columnList, legislator);
    }

    public Legislator read(long id){
        List<Legislator> legislators =
                DaoHelper.read(connection, table, columnList, Collections.singletonList(id), supplier);
        return DaoHelper.fromSingletonList(legislators, "Table " + table + ", ID " + id);
    }

    public List<Legislator> readAll(){
        List<Legislator> legislators =
                DaoHelper.read(connection, table, columnList, Collections.emptyList(), supplier);
        return legislators;
    }
}
