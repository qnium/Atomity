/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.backend.core;

/**
 *
 * @author Drozhin
 */
public class ConfigManager
{
    private static ConfigManager _instance = null;
    
    private String databaseURL;
    private String databaseDriverName;
    
    public static synchronized ConfigManager getInstance()
    {
        if ( _instance == null )
        {
            _instance = new ConfigManager();
        }
        
        return _instance;
    }
    
    private ConfigManager() {
    }
    
    public String getDatabaseURL() {
        return databaseURL;
    }

    public void setDatabaseURL(String databaseURL) {
        this.databaseURL = databaseURL;
    }
    
    public String getDatabaseDriverName() {
        return databaseDriverName;
    }

    public void setDatabaseDriverName(String databaseDriverName) {
        this.databaseDriverName = databaseDriverName;
    }
}
