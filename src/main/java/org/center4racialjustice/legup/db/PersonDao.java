package org.center4racialjustice.legup.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PersonDao {

    private static List<Column> columnList =
            Arrays.asList(
                    new Column<Person, Long>("ID", ColumnType.Long, Person::getId, Person::setId),
                    new Column<Person, String>("PREFIX", ColumnType.String, Person::getPrefix, Person::setPrefix),
                    new Column<Person, String>("FIRST_NAME", ColumnType.String, Person::getFirstName, Person::setFirstName),
                    new Column<Person, String>("MIDDLE_NAME", ColumnType.String, Person::getMiddleName, Person::setMiddleName),
                    new Column<Person, String>("LAST_NAME", ColumnType.String, Person::getLastName, Person::setLastName),
                    new Column<Person, String>("SUFFIX", ColumnType.String, Person::getSuffix, Person::setSuffix)
                    );

    private static List<String> columns =
            Arrays.asList("ID", "PREFIX", "FIRST_NAME", "MIDDLE_NAME", "LAST_NAME", "SUFFIX");

    private static String table = "persons";

    private final Connection connection;

    public PersonDao(Connection connection){
        this.connection = connection;
    }

    public String columnsAsString(){
        return String.join(", ", columns);
    }

    public long save(Person person) throws SQLException {
        if( person.getId() == null ){
            return insert(person);
        } else {
            return update(person);
        }
    }



    private String insertStatement(){
        StringBuilder bldr = new StringBuilder();
        for(int idx=0; idx<columnList.size()-2; idx++){
            bldr.append("?, ");
        }
        bldr.append("? ");

        String sql = "insert into " + table + " ( " + columnsAsString() + " ) "
                + " values ( DEFAULT, "
                + bldr.toString()
                + " ) "
                + " RETURNING ID ";
        return sql;
    }

    private long insert(Person person) throws SQLException {

        String sql = insertStatement();

        PreparedStatement statement = connection.prepareStatement(sql);

        for(int index= 1; index<columnList.size(); index++){
            Column column = columnList.get(index);
            ColumnType columnType = column.getColumnType();
            switch(columnType){
                case String:
                    Function<Person, String> stringGetter = column.getGetter();
                    statement.setString(index, stringGetter.apply(person));
                    break;
                case Long:
                    Function<Person, Long> longGetter = column.getGetter();
                    statement.setLong(index, longGetter.apply(person));
                    break;
            }

        }

        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        long id = resultSet.getLong("id");
        resultSet.close();
        statement.close();
        return id;
     }

    private long update(Person person) throws SQLException {
        String sql = "update " + table + " set "
                + " prefix = ?, "
                + " first_name = ?, "
                + " middle_name = ?, "
                + " last_name = ?, "
                + " suffix = ? "
                + " where id = " + person.getId()
                + " RETURNING ID";

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, person.getPrefix());
        statement.setString(2, person.getFirstName());
        statement.setString(3, person.getMiddleName());
        statement.setString(4, person.getLastName());
        statement.setString(5, person.getSuffix());

        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        long id = resultSet.getLong("id");
        resultSet.close();
        statement.close();
        return id;
    }

    public Person read(long id) throws SQLException {
        Statement statement = null;
        ResultSet resultSet = null;

        try {

            statement = connection.createStatement();

            String sql = "select " + columnsAsString() + " from " + table
                    + " where id = " + id;

            resultSet = statement.executeQuery(sql);

            if (resultSet.next()) {
                Person person = populate(resultSet);
                return person;
            } else {
                return null;
            }
        } finally {
            if( resultSet != null ){
                resultSet.close();
            }
            if( statement != null){
                statement.close();
            }
        }

    }

    private Person populate(ResultSet resultSet) throws SQLException {
        Person person = new Person();

        for (Column column: columnList) {
            String name = column.getName();
            ColumnType columnType = column.getColumnType();
            BiConsumer setter = column.getSetter();
            switch (columnType){
                case Long:
                    setter.accept(person, resultSet.getLong(name));
                    break;
                case String:
                    setter.accept(person, resultSet.getString(name));
                    break;
            }
        }

        return person;
    }

    public List<Person> readAll() throws SQLException {

        Statement statement = null;
        ResultSet resultSet = null;

        try {

            statement = connection.createStatement();

            resultSet = statement.executeQuery("select " + columnsAsString() + " from " + table);

            List<Person> personList = new ArrayList<>();

            while (resultSet.next()) {
                Person person = populate(resultSet);
                personList.add(person);
            }

            return personList;
        } finally {
            if( resultSet != null ){
                resultSet.close();
            }
            if( statement != null){
                statement.close();
            }
        }
    }

}