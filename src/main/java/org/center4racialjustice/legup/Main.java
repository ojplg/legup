package org.center4racialjustice.legup;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.app.Velocity;
import org.center4racialjustice.legup.db.BillMapper;
import org.center4racialjustice.legup.db.ConnectionPool;
import org.center4racialjustice.legup.domain.Bill;
import org.center4racialjustice.legup.web.ServerStarter;
import org.postgresql.ds.PGSimpleDataSource;
import org.postgresql.xa.PGXADataSource;

import javax.sql.DataSource;
import java.io.FileReader;
import java.io.Reader;
import java.util.Properties;

public class Main {

    private static final Logger log = LogManager.getLogger(Main.class);

    private static final Properties properties = new Properties();

    public static void main(String[] args){
        log.info("Starting");

        try {
            if (args.length > 0){
                log.info("Loading properies from " + args[0]);
                Reader reader = new FileReader(args[0]);
                properties.load(reader);
            } else {
                log.info("Using default properties");
                properties.setProperty("db.url","jdbc:postgresql://localhost:5432/legup");
                properties.setProperty("db.user","legupuser");
                properties.setProperty("db.password","legupuserpass");
            }

            // make sure postgres drivers are loaded
            Class.forName("org.postgresql.Driver");


            ConnectionPool pool = new ConnectionPool(
                    properties.getProperty("db.url"),
                    properties.getProperty("db.user"),
                    properties.getProperty("db.password")
            );

            SqlSessionFactory sqlSessionFactory = configureIbatis();
            testIbatis(sqlSessionFactory);

            Properties velocityProperties = new Properties();
            velocityProperties.put("resource.loader", "class");
            velocityProperties.put("class.resource.loader.class","org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            Velocity.init(velocityProperties);

            ServerStarter serverStarter = new ServerStarter(pool);
            serverStarter.start();
        } catch (Exception ex){
            System.out.println("Could not start server");
            ex.printStackTrace();
        }
    }

    private static void testIbatis(SqlSessionFactory sqlSessionFactory){

        try(SqlSession sqlSession = sqlSessionFactory.openSession()){
            BillMapper mapper = sqlSession.getMapper(BillMapper.class);

            Bill bill = mapper.selectBill(26);

            log.info("Found bill 26 !! " + bill);

        }

    }

    private static SqlSessionFactory configureIbatis() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setDatabaseName("legup");
        dataSource.setUser("legupuser");
        dataSource.setPassword("legupuserpass");
        // Might need server name and port (localhost and default)

        TransactionFactory transactionFactory =
                new JdbcTransactionFactory();
        Environment environment =
                new Environment("development", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(BillMapper.class);

        SqlSessionFactory sqlSessionFactory =
                new SqlSessionFactoryBuilder().build(configuration);

        log.info("**IBATIS** Configuration built " + configuration);
        log.info("**IBATIS** SQLSession Factory built " + sqlSessionFactory);

        return sqlSessionFactory;
    }


}
