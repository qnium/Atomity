/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.backend.core;

import com.j256.ormlite.dao.Dao;
import java.sql.SQLException;
import java.util.TimerTask;

/**
 *
 * @author Drozhin
 */
public class ConnectionUpdater<T> extends TimerTask
{
    private final Dao<T, Long> dao;
            
    public ConnectionUpdater(Dao<T, Long> dao)
    {
        this.dao = dao;
    }
    
    @Override
    public void run() {
        try {
            dao.queryRaw("SELECT 1");
        } catch (SQLException ex) {
            Logger.log.error("Connection updater error.", ex);
        }
    }    
}
