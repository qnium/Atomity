/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.paymentsystem.dao;

import com.qnium.common.backend.assets.interfaces.IEntityManager;
import com.qnium.common.backend.core.FieldFilter;
import com.qnium.common.backend.core.FilteredResult;
import com.qnium.common.paymentsystem.core.PaymentSystem;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Drozhin
 */
public class CustomerManager implements IEntityManager
{
    private static CustomerManager instance;
    
    public static synchronized CustomerManager getInstance() throws SQLException {
        if (instance == null) {
            instance = new CustomerManager();
        }
        return instance;
    }
    
    private CustomerManager() { }
    
    @Override
    public void create(Object entity) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public FilteredResult read(List filters, long startIndex, long count) throws Exception {
        return PaymentSystem.getProvider().customersRead(filters, startIndex, count);
    }

    @Override
    public void update(List entities) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(List entities) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long getCount() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }    
}
