package org.center4racialjustice.legup.db;

import java.sql.Connection;

public interface ConnectionFactory {
    Connection connect();
}
