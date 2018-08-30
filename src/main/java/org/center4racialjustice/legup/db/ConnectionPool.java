package org.center4racialjustice.legup.db;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionPool {

    private final String url;
    private final String user;
    private final String password;

    private SqlSessionFactory sqlSessionFactory;

    public ConnectionPool(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public Connection getConnection(){
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }

    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory){
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public SqlSession session(){
        return sqlSessionFactory.openSession();
    }

    public BillDao getBillDao() {
        // FIXME: the sql session needs to be closed
        SqlSession sqlSession = sqlSessionFactory.openSession();
        BillDao billDao = new BillDao(sqlSession);
        return billDao;
    }



}
