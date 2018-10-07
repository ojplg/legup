package org.center4racialjustice.legup.db;

import org.hrorm.Transactor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.function.Function;

public class ConnectionPool {

    private final ConnectionFactory connectionFactory;
    private final Transactor transactor;

    public ConnectionPool(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        this.transactor = new Transactor(connectionFactory::connect);
    }

    public void runAndCommit(Consumer<Connection> action){
        transactor.runAndCommit(action);
    }

    public <R> R runAndCommit(Function<Connection, R> function){
        return transactor.runAndCommit(function);
    }

    public <R> R useConnection(Function<Connection, R> function){
        Connection connection = null;
        try {
            connection = connectionFactory.connect();
            return function.apply(connection);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex){
                throw new RuntimeException(ex);
            }
        }
    }
}
