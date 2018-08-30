package org.center4racialjustice.legup.db;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.postgresql.ds.PGSimpleDataSource;

public class MybatisStarter {

    private static final Logger log = LogManager.getLogger(MybatisStarter.class);

    private final String name;
    private final String userName;
    private final String password;

    public MybatisStarter(String name, String userName, String password) {
        this.name = name;
        this.userName = userName;
        this.password = password;
    }

    public SqlSessionFactory sessionFactory() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setDatabaseName(name);
        dataSource.setUser(userName);
        dataSource.setPassword(password);
        // Might need server name and port (localhost and default)

        TransactionFactory transactionFactory =
                new JdbcTransactionFactory();
        Environment environment =
                new Environment("development", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(BillMapper.class);
        configuration.addMapper(LegislatorMapper.class);

        SqlSessionFactory sqlSessionFactory =
                new SqlSessionFactoryBuilder().build(configuration);

        log.info("**IBATIS** Configuration built " + configuration);
        log.info("**IBATIS** SQLSession Factory built " + sqlSessionFactory);

        return sqlSessionFactory;
    }


}
