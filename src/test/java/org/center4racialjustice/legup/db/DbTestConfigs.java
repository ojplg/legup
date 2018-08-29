package org.center4racialjustice.legup.db;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbTestConfigs {

    public static final String name = "legup_test";
    public static final String userName = "legupuser";
    public static final String password = "legupuserpass";

    private static final SqlSessionFactory sqlSessionFactory = sqlSessionFactory();

    public static Connection connect(){
        try {
            Class.forName("org.postgresql.Driver");

            return DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/legup_test","legupuser", "legupuserpass");

        } catch (ClassNotFoundException | SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static SqlSessionFactory sqlSessionFactory(){
        MybatisStarter starter = new MybatisStarter(name, userName, password);
        return starter.sessionFactory();
    }

    public static SqlSession session(){
        return sqlSessionFactory.openSession();
    }

}
