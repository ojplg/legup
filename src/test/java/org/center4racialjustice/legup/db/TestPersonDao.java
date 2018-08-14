package org.center4racialjustice.legup.db;

import org.junit.Assert;
import org.junit.Before;
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

        } catch (ClassNotFoundException | SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Before
    public void setUp() throws SQLException {
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

    @Test
    public void testReadById() throws SQLException {
        Connection connection = connect();
        PersonDao dao = new PersonDao(connection);

        Person person1 = new Person();
        person1.setFirstName("John");
        person1.setLastName("Smith");

        dao.save(person1);

        Person person2 = new Person();
        person2.setFirstName("Herbie");
        person2.setLastName("Johnson-McGee");

        long id = dao.save(person2);

        Person herbie = dao.read(id);
        Assert.assertNotNull(herbie);
        Assert.assertEquals("Herbie", herbie.getFirstName());
        Assert.assertEquals("Johnson-McGee", herbie.getLastName());
        connection.close();

    }

    @Test
    public void testDoesUpdates()  {
        Connection connection = connect();
        PersonDao dao = new PersonDao(connection);

        Person person1 = new Person();
        person1.setFirstName("John");
        person1.setLastName("Smith");

        dao.save(person1);

        Person person2 = new Person();
        person2.setFirstName("Herbie");
        person2.setLastName("Johnson-McGee");

        long id = dao.save(person2);

        Person herbie = dao.read(id);
        Assert.assertNotNull(herbie);
        Assert.assertEquals("Herbie", herbie.getFirstName());
        Assert.assertEquals("Johnson-McGee", herbie.getLastName());

        herbie.setFirstName("Herbietta");
        herbie.setMiddleName("Johnson");
        herbie.setLastName("McGee");

        dao.save(herbie);

        Person updatedHerbie = dao.read(id);
        Assert.assertNotNull(updatedHerbie);
        Assert.assertEquals("Herbietta", herbie.getFirstName());
        Assert.assertEquals("Johnson", herbie.getMiddleName());
        Assert.assertEquals("McGee", herbie.getLastName());

    }

}
