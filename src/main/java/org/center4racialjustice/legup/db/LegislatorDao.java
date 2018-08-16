package org.center4racialjustice.legup.db;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class LegislatorDao implements Dao<Legislator> {

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
        long personID = personDao.save(legislator.getPerson());
        Map<String, Long> ids = Collections.singletonMap("PERSON_ID", personID);
        return DaoHelper.save(connection, table, columnList, legislator, ids);
    }

    public Legislator read(long id){
        Function<Long, Person> personFinder = personDao::read;
        Map<String, Function> finders = Collections.singletonMap("PERSON_ID", personFinder);
        List<Legislator> legislators =
                DaoHelper.read(connection, table, columnList, Collections.singletonList(id), () -> new Legislator(), finders);
        return DaoHelper.fromSingletonList(legislators, "Table " + table + ", ID " + id);
    }

    public List<Legislator> readAll(){
        Function<Long, Person> personFinder = personDao::read;
        Map<String, Function> finders = Collections.singletonMap("PERSON_ID", personFinder);
        List<Legislator> legislators =
                DaoHelper.read(connection, table, columnList, Collections.emptyList(), () -> new Legislator(), finders);
        return legislators;
    }
}
