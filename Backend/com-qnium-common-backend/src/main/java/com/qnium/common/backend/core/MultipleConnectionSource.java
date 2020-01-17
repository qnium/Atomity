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
import java.util.ArrayList;

/**
 *
 * @author Kirill Zhukov
 */
public class MultipleConnectionSource implements ConnectionSource {

    private final String _databaseUrl;
    private final DatabaseType _dbType;
    //private DatabaseConnection dbConnection;
    private final int _maxConnections;
    
    private ArrayList<DatabaseConnection> _connections;
    
    public MultipleConnectionSource(String databaseUrl, int maxConnections) throws SQLException {
        _databaseUrl = databaseUrl;
        _dbType = DatabaseTypeUtils.createDatabaseType(databaseUrl);
        _dbType.loadDriver();
        _dbType.setDriver(DriverManager.getDriver(databaseUrl));
        _maxConnections = maxConnections;
        _connections = new ArrayList<>();
    }

    
    DatabaseConnection createConnection() throws SQLException
    {
        DatabaseConnection newDbConnection = new JdbcDatabaseConnection(DriverManager.getConnection(_databaseUrl));
        _connections.add(newDbConnection);
        newDbConnection.setAutoCommit(true);
        return newDbConnection;
    }
    
    @Override
    public DatabaseConnection getReadOnlyConnection() throws SQLException {
        return createConnection();
    }

    @Override
    public DatabaseConnection getReadWriteConnection() throws SQLException {
        return createConnection();
    }

    @Override
    public void releaseConnection(DatabaseConnection connection) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean saveSpecialConnection(DatabaseConnection connection) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clearSpecialConnection(DatabaseConnection connection) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DatabaseConnection getSpecialConnection() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void close() throws SQLException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void closeQuietly() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DatabaseType getDatabaseType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isOpen() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
