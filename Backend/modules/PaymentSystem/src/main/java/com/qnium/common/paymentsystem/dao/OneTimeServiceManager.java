/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.paymentsystem.dao;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.qnium.common.backend.assets.interfaces.IEntityManager;
import com.qnium.common.backend.core.ConfigManager;
import com.qnium.common.backend.core.FieldFilter;
import com.qnium.common.backend.core.FilteredResult;
import com.qnium.common.backend.core.EntityManager;
import com.qnium.common.paymentsystem.core.PaymentSystem;
import com.qnium.common.paymentsystem.dataobjects.Feature;
import com.qnium.common.paymentsystem.dataobjects.OneTimeService;
import com.qnium.common.paymentsystem.dataobjects.Payment;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Drozhin
 */
public class OneTimeServiceManager implements IEntityManager<OneTimeService>
{
    private static OneTimeServiceManager instance;
    
    private final Dao<OneTimeService, Long> oneTimeServiceDao;
    
    public static synchronized OneTimeServiceManager getInstance() throws SQLException {
        if (instance == null) {
            instance = new OneTimeServiceManager();
        }
        return instance;
    }
    
    private OneTimeServiceManager() throws SQLException
    {
        ConnectionSource connectionSource = new JdbcConnectionSource(ConfigManager.getInstance().getDatabaseURL());
        
        oneTimeServiceDao = DaoManager.createDao(connectionSource, OneTimeService.class);
        if (!oneTimeServiceDao.isTableExists()) {
            TableUtils.createTable(connectionSource, OneTimeService.class);
        }
    }
    
    @Override
    public void create(OneTimeService entity) throws Exception {
        EntityManager.getInstance(OneTimeService.class).create(entity);
    }

    @Override
    public FilteredResult<OneTimeService> read(List<FieldFilter> filters, long startIndex, long count) throws Exception
    {
        FilteredResult<OneTimeService> oneTimeServices = EntityManager.getInstance(OneTimeService.class).read(filters, startIndex, count);
                
        List<Long> oneTimeServiceIds = new ArrayList();
        
        for(OneTimeService oneTimeService : oneTimeServices.data){
            oneTimeServiceIds.add(oneTimeService.featureId);
        }
        
        List<Feature> features = PaymentSystem.getProvider().getFeatures(oneTimeServiceIds);
        
        for(OneTimeService oneTimeService : oneTimeServices.data){
            oneTimeService.feature = features.stream()
                .filter(feature -> feature.id == oneTimeService.featureId)
                .findAny()
                .orElse(null);
        }
        
        return oneTimeServices;
    }

    @Override
    public void update(List<OneTimeService> entities) throws Exception {
        EntityManager.getInstance(OneTimeService.class).update(entities);
    }

    @Override
    public void delete(List<OneTimeService> entities) throws Exception {
        EntityManager.getInstance(OneTimeService.class).delete(entities);
    }

    @Override
    public long getCount() throws Exception {
        return EntityManager.getInstance(OneTimeService.class).getCount();
    }
}
