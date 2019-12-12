/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.backend.core;

import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.db.DatabaseTypeUtils;
import com.j256.ormlite.jdbc.JdbcDatabaseConnection;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author DEV008
 */
public class RestorableConnectionSource implements ConnectionSource {

    private final String databaseUrl;
    private final DatabaseType dbType;
    private DatabaseConnection dbConnection;

    public RestorableConnectionSource(String databaseUrl) throws SQLException {
        this.databaseUrl = databaseUrl;
        dbType = DatabaseTypeUtils.createDatabaseType(databaseUrl);
        dbType.loadDriver();
        dbType.setDriver(DriverManager.getDriver(databaseUrl));
    }

    private DatabaseConnection createConnection() throws SQLException {
        DatabaseConnection newDbConnection = new JdbcDatabaseConnection(DriverManager.getConnection(databaseUrl));
        newDbConnection.setAutoCommit(true);
        return newDbConnection;
    }
    
    private DatabaseConnection getOrCreateConnection() throws SQLException {
        if (dbConnection == null || dbConnection.isClosed()) {
            dbConnection = createConnection();
        } else {
            try {
                dbConnection.executeStatement(dbType.getPingStatement(), DatabaseConnection.DEFAULT_RESULT_FLAGS);
            } catch(SQLException ex) {
                Logger.log.error("Ping failed opened DB connection, create new connection.");
                dbConnection.closeQuietly();
                dbConnection = createConnection();
            }
        }

        return dbConnection;
    }

    @Override
    public DatabaseConnection getReadOnlyConnection() throws SQLException {
        return getReadWriteConnection();
    }

    @Override
    public DatabaseConnection getReadWriteConnection() throws SQLException {
        return getOrCreateConnection();
    }

    @Override
    public void releaseConnection(DatabaseConnection connection) throws SQLException {
//        not used
    }

    @Override
    public boolean saveSpecialConnection(DatabaseConnection connection) throws SQLException {
//        not used
        return true;
    }

    @Override
    public void clearSpecialConnection(DatabaseConnection connection) {
//        not used
    }

    @Override
    public DatabaseConnection getSpecialConnection() {
        try {
            return getOrCreateConnection();
        } catch (SQLException ex) {
            Logger.log.error("Get special connection error.", ex);
            return null;
        }
    }

    @Override
    public void close() throws SQLException {
        if (dbConnection != null) {
            dbConnection.close();
        }
    }

    @Override
    public void closeQuietly() {
        if (dbConnection != null) {            
            dbConnection.closeQuietly();
        }
    }

    @Override
    public DatabaseType getDatabaseType() {
        return dbType;
    }

    @Override
    public boolean isOpen() {
        try {
            return dbConnection != null && !dbConnection.isClosed();
        } catch (SQLException ex) {
            Logger.log.error("IsOpen method error.", ex);
            return false;
        }
        
    }
}
