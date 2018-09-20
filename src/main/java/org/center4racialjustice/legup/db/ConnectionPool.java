package org.center4racialjustice.legup.db;

public class ConnectionPool {

    private final ConnectionFactory connectionFactory;

    public ConnectionPool(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public ConnectionWrapper getWrappedConnection(){
        return new ConnectionWrapper(connectionFactory.connect());
    }

}
