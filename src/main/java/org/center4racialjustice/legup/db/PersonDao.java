package org.center4racialjustice.legup.db;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PersonDao {

    private static String table = "persons";

    private static List<Column> columnList =
            Arrays.asList(
                    new Column<>("ID", ColumnType.Long, Person::getId, Person::setId),
                    new Column<>("PREFIX", ColumnType.String, Person::getPrefix, Person::setPrefix),
                    new Column<>("FIRST_NAME", ColumnType.String, Person::getFirstName, Person::setFirstName),
                    new Column<>("MIDDLE_NAME", ColumnType.String, Person::getMiddleName, Person::setMiddleName),
                    new Column<>("LAST_NAME", ColumnType.String, Person::getLastName, Person::setLastName),
                    new Column<>("SUFFIX", ColumnType.String, Person::getSuffix, Person::setSuffix)
                    );


    private final Connection connection;

    public PersonDao(Connection connection){
        this.connection = connection;
    }

    public long save(Person person) {
        if( person.getId() == null ){
            return insert(person);
        } else {
            return update(person);
        }
    }

    private long insert(Person person) {
        return DaoHelper.doInsert(person, table, columnList, connection);
    }

    private long update(Person person) {
        return DaoHelper.doUpdate(person, table, columnList, connection);
    }

    public Person read(long id) {
        List<Person> persons = DaoHelper.read(connection, table, columnList, Collections.singletonList(id), () -> new Person());
        if (persons.isEmpty()){
            return null;
        }
        if( persons.size() == 1){
            return persons.get(0);
        }
        throw new RuntimeException("Found " + persons.size() + " items with id " + id);
    }

    public List<Person> readAll() {
        return DaoHelper.read(connection, table, columnList, Collections.emptyList(), () -> new Person());
    }

}