/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.paymentsystem.dao;

import com.qnium.common.backend.assets.interfaces.IEntityManager;
import com.qnium.common.backend.core.EntityManager;
import com.qnium.common.backend.core.FieldFilter;
import com.qnium.common.backend.core.FilteredResult;
import com.qnium.common.paymentsystem.dataobjects.ChargeableService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Drozhin
 */
public class ChargeableServiceManager implements IEntityManager<ChargeableService>
{
    private static ChargeableServiceManager instance;
    private static EntityManager<ChargeableService> man;
    
    public static synchronized ChargeableServiceManager getInstance() throws SQLException {
        if (instance == null) {
            instance = new ChargeableServiceManager();
            man = EntityManager.getInstance(ChargeableService.class);
        }
        return instance;
    }
    
    private ChargeableServiceManager() { }

    @Override
    public void create(ChargeableService entity) throws Exception {
        entity.price = new BigDecimal(entity.price).setScale(2, RoundingMode.UP).doubleValue();
        man.create(entity);
    }

    @Override
    public FilteredResult<ChargeableService> read(List<FieldFilter> filters, long startIndex, long count) throws Exception {
        return man.read(filters, startIndex, count);
    }

    @Override
    public void update(List<ChargeableService> entities) throws Exception {
        entities.stream().forEach((entity) -> {
            entity.price = new BigDecimal(entity.price).setScale(2, RoundingMode.UP).doubleValue();
        });
        man.update(entities);
    }

    @Override
    public void delete(List<ChargeableService> entities) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long getCount() throws Exception {
        return man.getCount();
    }    
}
