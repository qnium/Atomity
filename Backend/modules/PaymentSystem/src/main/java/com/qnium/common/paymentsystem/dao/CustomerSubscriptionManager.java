/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.paymentsystem.dao;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.qnium.common.backend.assets.definitions.FieldOperations;
import com.qnium.common.backend.assets.interfaces.IEntityManager;
import com.qnium.common.backend.core.ConfigManager;
import com.qnium.common.backend.core.FieldFilter;
import com.qnium.common.backend.core.FilteredResult;
import com.qnium.common.paymentsystem.core.PaymentSystem;
import com.qnium.common.paymentsystem.dataobjects.Customer;
import com.qnium.common.paymentsystem.dataobjects.CustomerSubscriptionAssignment;
import com.qnium.common.paymentsystem.dataobjects.Subscription;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Nikita
 */
public class CustomerSubscriptionManager implements IEntityManager<CustomerSubscriptionAssignment>{

    /**
     * This value is close to maximum date value in MySQL. Used for saving
     * max date to DB.
     */
    private final Long MAX_DATE = 253370764800000L;
    
    private static CustomerSubscriptionManager instance;
    
    private Dao<CustomerSubscriptionAssignment, Long> orgSubscriptionDao;
    //private Dao<Customer, Long> orgDao;
    private Dao<Subscription, Long> SubscriptionDao;
    
    public static synchronized CustomerSubscriptionManager getInstance() throws SQLException {
        if (instance == null) {
            instance = new CustomerSubscriptionManager();
        }
        return instance;
    }
    private CustomerSubscriptionManager() throws SQLException {
        String databaseUrl = ConfigManager.getInstance().getDatabaseURL();
        ConnectionSource connectionSource = new JdbcConnectionSource(databaseUrl);
        
        orgSubscriptionDao = DaoManager.createDao(connectionSource, CustomerSubscriptionAssignment.class);        
        if (!orgSubscriptionDao.isTableExists()) {
            TableUtils.createTable(connectionSource, CustomerSubscriptionAssignment.class);
        }
        
        SubscriptionDao = DaoManager.createDao(connectionSource, Subscription.class);
        
        if (!SubscriptionDao.isTableExists()) {
            TableUtils.createTable(connectionSource, Subscription.class);
        }
    }
    @Override
    public void create(CustomerSubscriptionAssignment entity) throws Exception {
        orgSubscriptionDao.create(entity);
    }

    @Override
    public FilteredResult<CustomerSubscriptionAssignment> read(List<FieldFilter> filters, long startIndex, long count) throws Exception {
        QueryBuilder<CustomerSubscriptionAssignment, Long> orgSubscriptionQB = orgSubscriptionDao.queryBuilder();
        if (filters != null && filters.size() > 0) {
            Where<CustomerSubscriptionAssignment, Long> orgSubscriptionWhere = orgSubscriptionQB.where();
            orgSubscriptionWhere.isNotNull(CustomerSubscriptionAssignment.ID);
            for (FieldFilter f : filters) {
                if (f.value != null && !f.value.toString().isEmpty()) {
                    orgSubscriptionWhere.and();
//                    if("organization_name".equals(f.field))
//                    {
//                        //OrganizationsManager om = OrganizationsManager.getInstance();
//                        //FilteredResult<Customer> orgs = om.read(new FieldFilter[]{f}, startIndex, count);
//                        
//                        List<FieldFilter> additionFilter = new ArrayList<FieldFilter>();
//                        additionFilter.add(f);
//                        //FilteredResult<Customer> orgs = PaymentSystem.getProvider().customersRead(additionFilter, startIndex, count);
//                        FilteredResult<Customer> orgs = PaymentSystem.getProvider().customersRead(additionFilter, 0, 0);
//                        List<Long> ids = new ArrayList<>();
//                        for(Customer o : orgs.data)
//                        {
//                            ids.add(o.id);
//                        }
//                        if(ids.size() > 0){
//                            orgSubscriptionWhere.in(CustomerSubscriptionAssignment.CUSTOMER_ID, ids);
//                        }
//                        continue;
//                    }
                    
                    switch (f.operation) {
                        case FieldOperations.EQ:
                            orgSubscriptionWhere.eq(f.field, f.value);
                            break;
                        case FieldOperations.LIKE:
                            orgSubscriptionWhere.like(f.field, "%" + f.value + "%");
                            break;
                    }
                }
            }
        }
        FilteredResult<CustomerSubscriptionAssignment> result = new FilteredResult<>();
        result.totalCounter = orgSubscriptionQB.countOf();
        orgSubscriptionQB.setCountOf(false);

        if (count > 0) {
            orgSubscriptionQB = orgSubscriptionQB.offset(startIndex).limit(count);
        }
        result.data = orgSubscriptionQB.query();

        List<Long> customerIds = result.data.stream().map(m -> m.customerId).collect(Collectors.toList());
        List<Customer> customers = PaymentSystem.getProvider().getCustomers(customerIds);
        
        for(CustomerSubscriptionAssignment osa : result.data)
        {
            osa.customer = customers.stream()
                    .filter(customer -> customer.id == osa.customerId )
                    .findAny()
                    .orElse(null);
            
            SubscriptionDao.refresh(osa.subscription);
        }
        
        return result;
    }

    @Override
    public void update(List<CustomerSubscriptionAssignment> entities) throws Exception {
        for(CustomerSubscriptionAssignment osa : entities)
        {
            DeleteBuilder<CustomerSubscriptionAssignment, Long> osaDB = orgSubscriptionDao.deleteBuilder();
            osaDB.where().eq(CustomerSubscriptionAssignment.CUSTOMER_ID, osa.customerId);
            osaDB.delete();            
        }
        for(CustomerSubscriptionAssignment osa : entities)
        {
            orgSubscriptionDao.create(osa);
        }
    }

    @Override
    public void delete(List<CustomerSubscriptionAssignment> entities) throws Exception {
        throw new Exception("Not implemented");
//        List<Long> ids = new ArrayList<>();
//        for (OrgSubscriptionAssignment e : entities) {
//            ids.add(e.id);
//        }
//        DeleteBuilder<OrgSubscriptionAssignment, Long> subDB = orgSubscriptionDao.deleteBuilder();        
//        subDB.where().in(OrgSubscriptionAssignment.ID, ids);
//        subDB.delete();
    }

    @Override
    public long getCount() throws Exception {
        return orgSubscriptionDao.countOf();
    }

    public void setDefaultSubscription(Customer org) throws Exception
    {
        QueryBuilder<Subscription, Long> subQB = SubscriptionDao.queryBuilder();        
        Subscription subscription = subQB.where().eq(Subscription.DEFAULT, true).queryForFirst();
        CustomerSubscriptionAssignment result = new CustomerSubscriptionAssignment();
        result.customerId = org.id;
        result.customer = org;
        result.subscription = subscription;
        result.startTime = new Date(0);
        result.endTime = new Date(MAX_DATE);
        create(result);
    }    
        
//    public void removeSubscriptionsForOrganizations(List<Long> orgIds) throws Exception
//    {
//        DeleteBuilder<OrgSubscriptionAssignment, Long> subDB = orgSubscriptionDao.deleteBuilder();        
//        subDB.where().in(OrgSubscriptionAssignment.CUSTOMER_ID, orgIds);
//        subDB.delete();
//    }    
}
