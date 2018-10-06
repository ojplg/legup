package org.center4racialjustice.legup.db;

import org.hrorm.HrormException;

import java.util.function.Consumer;
import java.util.function.Function;

public class ConnectionPool {

    private final ConnectionFactory connectionFactory;

    public ConnectionPool(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public ConnectionWrapper getWrappedConnection(){
        return new ConnectionWrapper(connectionFactory.connect());
    }

    public void runAndCommit(Consumer<ConnectionWrapper> action){
        ConnectionWrapper connectionWrapper = null;
        try {
            connectionWrapper = getWrappedConnection();
            action.accept(connectionWrapper);
            connectionWrapper.commit();
        } catch (HrormException ex){
            connectionWrapper.rollback();
            throw ex;
        } finally {
            connectionWrapper.close();
        }
    }

    public <R> R runAndCommit(Function<ConnectionWrapper, R> function){
        ConnectionWrapper connectionWrapper = null;
        try {
            connectionWrapper = getWrappedConnection();
            R result = function.apply(connectionWrapper);
            connectionWrapper.commit();
            return result;
        } catch (HrormException ex){
            connectionWrapper.rollback();
            throw ex;
        } finally {
            connectionWrapper.close();
        }
    }
}
