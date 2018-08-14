package org.center4racialjustice.legup.db;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LegislatorDao {

    private static String table = "legislators";

    private static List<Column> columnList =
            Arrays.asList(
                    new Column<>("ID", ColumnType.Long, Legislator::getId, Legislator::setId),
                    new Column<>("DISTRICT", ColumnType.Long, Legislator::getDistrict, Legislator::setDistrict),
                    new Column<>("PARTY", ColumnType.String, Legislator::getParty, Legislator::setParty),
                    new Column<>("ASSEMBLY", ColumnType.String, Legislator::getAssembly, Legislator::setAssembly),
                    new Column<>("YEAR", ColumnType.Long, Legislator::getYear, Legislator::setYear),
                    new Column<>("PERSON_ID", ColumnType.Reference, Legislator::getPerson, Legislator::setPerson)
            );

    private final Dao<Person> personDao;
    private final Connection connection;

    public LegislatorDao(Connection connection){
        this.connection = connection;
        personDao = new PersonDao(connection);
    }

    public long save(Legislator legislator){
        return DaoHelper.save(legislator, table, columnList, connection);
    }

    public Legislator read(long id){
        List<Legislator> legislators =
                DaoHelper.read(connection, table, columnList, Collections.singletonList(id), () -> new Legislator());
        return DaoHelper.fromSingletonList(legislators, "Table " + table + ", ID " + id);
    }
}
