package org.center4racialjustice.legup.db;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class TestPersonDao {

    private static Connection connect(){
        try {
            Class.forName("org.postgresql.Driver");

            return DriverManager.getConnection(
                            "jdbc:postgresql://localhost:5432/legup","legupuser", "legupuserpass");

        } catch (ClassNotFoundException cnfe){
            throw new RuntimeException(cnfe);
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @BeforeClass
    public static void setUp() throws SQLException {
        clearTable();
    }

    private static void clearTable() throws SQLException {
        Connection connection = connect();
        Statement statement = connection.createStatement();
        statement.execute("delete from persons");
        statement.close();
        connection.close();
    }

    @Test
    public void testReadAll() throws SQLException {
        Connection connection = connect();
        PersonDao dao = new PersonDao(connection);

        Person person1 = new Person();
        person1.setFirstName("John");
        person1.setLastName("Smith");

        dao.save(person1);

        Person person2 = new Person();
        person2.setFirstName("Herbie");
        person2.setLastName("Johnson-McGee");

        dao.save(person2);
        
        List<Person> personList = dao.readAll();
        Assert.assertNotNull(personList);
        Assert.assertEquals(2, personList.size());
        connection.close();
    }

}
