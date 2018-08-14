package org.center4racialjustice.legup.db;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class TestPersonDao {

    private Connection connect(){
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

    @Test
    public void testReadAll() throws SQLException {
        Connection connection = connect();
        PersonDao dao = new PersonDao(connection);
        List<Person> personList = dao.readAll();
        Assert.assertNotNull(personList);
        Assert.assertEquals(1, personList.size());
        connection.close();
    }

}
