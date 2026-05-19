package com.airline.util;

import com.airline.exception.AirlineException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Functional interface defining database operations executed inside an atomic transaction.
 * @param <T> Return type of the transaction execution
 */
@FunctionalInterface
public interface TransactionCallback<T> {
    T execute(Connection conn) throws SQLException, AirlineException;
}
