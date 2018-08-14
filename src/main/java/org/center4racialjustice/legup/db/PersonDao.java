package org.center4racialjustice.legup.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PersonDao {

    private static List<String> columns =
            Arrays.asList("ID", "PREFIX", "FIRST_NAME", "MIDDLE_NAME", "LAST_NAME", "SUFFIX");

    private static String table = "persons";

    private static String sequence = "person_seq";

    private final Connection connection;

    public PersonDao(Connection connection){
        this.connection = connection;
    }

    public String columnsAsString(){
        return String.join(", ", columns);
    }

    public void save(Person person) throws SQLException {
        if( person.getId() == null ){
            insert(person);
        } else {
            update(person);
        }
    }

    private void insert(Person person) throws SQLException {

        String sql = "insert into " + table + " ( " + columnsAsString() + " ) "
                + " values ( DEFAULT, "
                + "?, ?, ?, ?, ? )";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, person.getPrefix());
        statement.setString(2, person.getFirstName());
        statement.setString(3, person.getMiddleName());
        statement.setString(4, person.getLastName());
        statement.setString(5, person.getSuffix());

        statement.execute();
        statement.close();
     }

    private void update(Person person) throws SQLException {
        String sql = "update " + table + " set "
                + " prefix = ? "
                + " first_name = ? "
                + " middle_name = ? "
                + " last_name = ? "
                + " suffix = ? "
                + " where id = " + person.getId();

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, person.getPrefix());
        statement.setString(2, person.getFirstName());
        statement.setString(3, person.getMiddleName());
        statement.setString(4, person.getLastName());
        statement.setString(5, person.getSuffix());

        statement.execute();
        statement.close();

    }

    private Person read(long id) throws SQLException {
        Statement statement = null;
        ResultSet resultSet = null;

        try {

            statement = connection.createStatement();

            String sql = "select " + columnsAsString() + " from " + table
                    + " where id = " + id;

            resultSet = statement.executeQuery(sql);

            if (resultSet.next()) {

                String prefix = resultSet.getString("PREFIX");
                String firstName = resultSet.getString("FIRST_NAME");
                String middleName = resultSet.getString("MIDDLE_NAME");
                String lastName = resultSet.getString("LAST_NAME");
                String suffix = resultSet.getString("SUFFIX");

                Person person = new Person();
                person.setId(id);
                person.setPrefix(prefix);
                person.setFirstName(firstName);
                person.setMiddleName(middleName);
                person.setLastName(lastName);
                person.setSuffix(suffix);

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

    public List<Person> readAll() throws SQLException {

        Statement statement = null;
        ResultSet resultSet = null;

        try {

            statement = connection.createStatement();

            resultSet = statement.executeQuery("select " + columnsAsString() + " from " + table);

            List<Person> personList = new ArrayList<>();

            while (resultSet.next()) {
                long id = resultSet.getLong("ID");
                String prefix = resultSet.getString("PREFIX");
                String firstName = resultSet.getString("FIRST_NAME");
                String middleName = resultSet.getString("MIDDLE_NAME");
                String lastName = resultSet.getString("LAST_NAME");
                String suffix = resultSet.getString("SUFFIX");

                Person person = new Person();
                person.setId(id);
                person.setPrefix(prefix);
                person.setFirstName(firstName);
                person.setMiddleName(middleName);
                person.setLastName(lastName);
                person.setSuffix(suffix);

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