package org.center4racialjustice.legup.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PersonDao {

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