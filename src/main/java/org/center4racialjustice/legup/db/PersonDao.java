package org.center4racialjustice.legup.db;

import java.sql.Connection;
import java.util.Arrays;
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


    private final SimpleDao<Person> dao;

    public PersonDao(Connection connection){
        this.dao = new SimpleDao<>(connection, () -> new Person(), table, columnList);
    }

    public long save(Person person) {
        return dao.save(person);
    }

    public Person read(long id) {
        return dao.read(id);
    }

    public List<Person> readAll() {
        return dao.readAll();
    }

}