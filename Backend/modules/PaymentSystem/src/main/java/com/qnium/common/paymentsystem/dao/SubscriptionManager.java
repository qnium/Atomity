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
import com.qnium.common.paymentsystem.dataobjects.ChargeableService;
import com.qnium.common.paymentsystem.dataobjects.Customer;
import com.qnium.common.paymentsystem.dataobjects.Feature;
import com.qnium.common.paymentsystem.dataobjects.CustomerSubscriptionAssignment;
import com.qnium.common.paymentsystem.dataobjects.Payment;
import com.qnium.common.paymentsystem.dataobjects.Subscription;
import com.qnium.common.paymentsystem.dataobjects.SubscriptionInfo;
import com.qnium.common.paymentsystem.dataobjects.SubscriptionFeatureAssignment;
import com.qnium.common.paymentsystem.definitions.ChargeableServiceType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author
 */
public class SubscriptionManager implements IEntityManager<Subscription>{
    
    private static SubscriptionManager instance;
    
    /**
     * This value is close to maximum date value in MySQL. Used for saving
     * max date to DB.
     */
    private final Long MAX_DATE = 253370764800000L;
    
    private final Dao<Subscription, Long> subscriptionDao;
    private final Dao<SubscriptionFeatureAssignment, Long> subscriptionFeatureAssignmentDao;
    private final Dao<CustomerSubscriptionAssignment, Long> customerSubscriptionAssignmentDao;
    private final Dao<ChargeableService, Long> chargeableServiceDao;
    private final Dao<Payment, Long> paymentDao;
            
    public static synchronized SubscriptionManager getInstance() throws SQLException {
        if (instance == null) {
            instance = new SubscriptionManager();
        }
        return instance;
    }
    private SubscriptionManager() throws SQLException {
        String databaseUrl = ConfigManager.getInstance().getDatabaseURL();
        ConnectionSource connectionSource = new JdbcConnectionSource(databaseUrl);
        
        subscriptionDao = DaoManager.createDao(connectionSource, Subscription.class);
        if (!subscriptionDao.isTableExists()) {
            TableUtils.createTable(connectionSource, Subscription.class);
        }
        
        subscriptionFeatureAssignmentDao = DaoManager.createDao(connectionSource, SubscriptionFeatureAssignment.class);
        if (!subscriptionFeatureAssignmentDao.isTableExists()) {
            TableUtils.createTable(connectionSource, SubscriptionFeatureAssignment.class);
        }
        
        customerSubscriptionAssignmentDao = DaoManager.createDao(connectionSource, CustomerSubscriptionAssignment.class);
        if(!customerSubscriptionAssignmentDao.isTableExists()){
            TableUtils.createTable(connectionSource, CustomerSubscriptionAssignment.class);
        }
        
        chargeableServiceDao = DaoManager.createDao(connectionSource, ChargeableService.class);
        if(!chargeableServiceDao.isTableExists()){
            TableUtils.createTable(connectionSource, ChargeableService.class);
        }

        paymentDao = DaoManager.createDao(connectionSource, Payment.class);
        if(!paymentDao.isTableExists()){
            TableUtils.createTable(connectionSource, Payment.class);
        }
    }
    
    @Override
    public void create(Subscription entity) throws Exception
    {
        subscriptionDao.create(entity);
        for (Feature ser : entity.features) {
            SubscriptionFeatureAssignment ssa = new SubscriptionFeatureAssignment();
            ssa.subscription = entity;
            ssa.featureId = ser.id;
            subscriptionFeatureAssignmentDao.create(ssa);
        }
        
        if(entity._default)
        {
            setDefault(entity);
        }
    }
    
    public void setDefault(Subscription newDefaultSubscription) throws SQLException, Exception
    {
        Subscription previosDefaultSubscription = new Subscription();
        
        List<Subscription> sub = subscriptionDao.queryForAll();
        for(Subscription s : sub)
        {            
            if(s._default)
                previosDefaultSubscription = s;
            
            s._default = false;
            subscriptionDao.update(s);
        }
        
        newDefaultSubscription._default = true;
        subscriptionDao.update(newDefaultSubscription);
        
        QueryBuilder<CustomerSubscriptionAssignment, Long> subscriptionAssignmentQB = customerSubscriptionAssignmentDao.queryBuilder();
        Where<CustomerSubscriptionAssignment, Long> subscriptionAssignmentWH = subscriptionAssignmentQB.where();
        subscriptionAssignmentWH.eq(CustomerSubscriptionAssignment.SUBSCRIPTION_ID, previosDefaultSubscription.id);
        
        List<CustomerSubscriptionAssignment> orgsWithPreviusDefaultSubscription = subscriptionAssignmentQB.query();
        
        for(CustomerSubscriptionAssignment subscriptionAssignemt : orgsWithPreviusDefaultSubscription)
        {
            subscriptionAssignemt.subscription.id = newDefaultSubscription.id;
            customerSubscriptionAssignmentDao.update(subscriptionAssignemt);
        }
        
        List<CustomerSubscriptionAssignment> orgsWithSubscription = customerSubscriptionAssignmentDao.queryForAll();
        List<Long> orgsIdWithSubscription = new ArrayList<>();
        
        orgsWithSubscription.stream().forEach((orgWithSubscription) -> {
            orgsIdWithSubscription.add((long)orgWithSubscription.customerId);
        });
        
        List<Customer> orgsWhitoutSubscription = PaymentSystem.getProvider().getCustomersExcludedFromIdList(orgsIdWithSubscription);
        
        CustomerSubscriptionManager orgManager = CustomerSubscriptionManager.getInstance();
        
        for(Customer orgWithoutSubscription : orgsWhitoutSubscription){
            orgManager.setDefaultSubscription(orgWithoutSubscription);
        }
    }

    @Override
    public FilteredResult<Subscription> read(List<FieldFilter> filters, long startIndex, long count) throws Exception {
        QueryBuilder<Subscription, Long> subscriptionQB = subscriptionDao.queryBuilder();
        if (filters != null && filters.size() > 0) {
            Where<Subscription, Long> subscriptionWhere = subscriptionQB.where();
            subscriptionWhere.isNotNull(Subscription.ID);
            for (FieldFilter f : filters) {
                if (f.value != null && !f.value.toString().isEmpty()) {
                    subscriptionWhere.and();
                    switch (f.operation) {
                        case FieldOperations.EQ:
                            subscriptionWhere.eq(f.field, f.value);
                            break;
                        case FieldOperations.LIKE:
                            subscriptionWhere.like(f.field, "%" + f.value + "%");
                            break;
                    }
                }
            }
        }
        FilteredResult<Subscription> result = new FilteredResult<>();
        result.totalCounter = subscriptionQB.countOf();
        subscriptionQB.setCountOf(false);

        if (count > 0) {
            subscriptionQB = subscriptionQB.offset(startIndex).limit(count);
        }
        result.data = subscriptionQB.query();
        return result;
    }

    @Override
    public void update(List<Subscription> entities) throws Exception {
        for (Subscription s : entities) {
            subscriptionDao.update(s);
            DeleteBuilder<SubscriptionFeatureAssignment, Long> subSerDB = subscriptionFeatureAssignmentDao.deleteBuilder();
            subSerDB.where().eq(SubscriptionFeatureAssignment.SUBSCRIPTION_ID, s.id);
            subSerDB.delete();
            for (Feature ser : s.features) {
                SubscriptionFeatureAssignment ssa = new SubscriptionFeatureAssignment();
                ssa.subscription = s;
                ssa.featureId = ser.id;                
                subscriptionFeatureAssignmentDao.create(ssa);
            }
            if(s._default)
            {
                setDefault(s);
            }
        }
    }

    @Override
    public void delete(List<Subscription> entities) throws Exception {
        for(Subscription subscription : entities){
            if(subscription._default){
                throw new Exception("IS_DEFAULT_SUBSCRIPTION");
            }            
            if(isPaidSubscription(subscription.id)){
                throw new Exception("IS_PAID_SUBSCRIPTION");
            }
            subscriptionDao.delete(subscription);
        }
    }

    @Override
    public long getCount() throws Exception {
        return subscriptionDao.countOf();
    }

    public Feature[] getFeaturesBySubscriptionId(long subscriptionId) throws SQLException, Exception {
        List<SubscriptionFeatureAssignment> 
                subSer = subscriptionFeatureAssignmentDao.queryForEq(SubscriptionFeatureAssignment.SUBSCRIPTION_ID, subscriptionId);
        
        List<Long> featureIds = subSer.stream().map(m -> m.featureId).collect(Collectors.toList());
        
        return PaymentSystem.getProvider().getFeatures(featureIds).toArray(new Feature[0]);
    }

    public Feature[] getFeatures() throws Exception {
        return PaymentSystem.getProvider().getAllFeatures().toArray(new Feature[0]);
    }
    
    public boolean isPaidSubscription(long subscriptionId) throws SQLException
    {        
        List<ChargeableService> chargeableServices = chargeableServiceDao.queryBuilder().where()
                .eq(ChargeableService.TYPE, ChargeableServiceType.SUBSCRIPTION.getValue())
                .and()
                .eq(ChargeableService.REFERENCE_ID, subscriptionId)
                .query();

        List<Long> chargeableServiceIds = new ArrayList<>();

        chargeableServices.stream().forEach((chargeableService) -> {
            chargeableServiceIds.add(chargeableService.id);
        });

        List<Payment> payments = new ArrayList();
        if(chargeableServiceIds.size() > 0){
            payments = paymentDao.queryBuilder().where().in(Payment.CHARGEABLE_SERVICE_ID, chargeableServiceIds).query();
        }
        
        return payments.size() > 0;
    }
    
    public SubscriptionInfo getCurrentSubscription(long customerId) throws SQLException{
        
        SubscriptionInfo currentSubscriptionInfo = new SubscriptionInfo();
        
        currentSubscriptionInfo.subscription = subscriptionDao
                .queryBuilder().where().eq(Subscription.DEFAULT, true).queryForFirst();
        
        CustomerSubscriptionAssignment orgSubscriptionAssigment = customerSubscriptionAssignmentDao.
            queryBuilder().where().eq(CustomerSubscriptionAssignment.CUSTOMER_ID, customerId).queryForFirst();
        subscriptionDao.refresh(orgSubscriptionAssigment.subscription);
        
        Date currentDate = new Date();
        
        // at the first place !!!
        if(orgSubscriptionAssigment.startTime.before(currentDate) && orgSubscriptionAssigment.endTime.before(currentDate))
        {
            updateOrgSubscriptionAssignment(customerId);
            
            orgSubscriptionAssigment = customerSubscriptionAssignmentDao.
                queryBuilder().where().eq(CustomerSubscriptionAssignment.CUSTOMER_ID, customerId).queryForFirst();
            subscriptionDao.refresh(orgSubscriptionAssigment.subscription);
        }

        // at the second place !!!
        if((orgSubscriptionAssigment.startTime.before(currentDate) || orgSubscriptionAssigment.startTime == currentDate) &&
           (orgSubscriptionAssigment.endTime.after(currentDate) || orgSubscriptionAssigment.endTime == currentDate))
        {
            //currentSubscriptionInfo.subscription = subscriptionDao.queryBuilder().where().eq(Subscription.ID, orgSubscriptionAssigment.subscription.id).queryForFirst();
            currentSubscriptionInfo.subscription = orgSubscriptionAssigment.subscription;
            currentSubscriptionInfo.startDate = orgSubscriptionAssigment.startTime;
            currentSubscriptionInfo.endDate = orgSubscriptionAssigment.endTime;
        }
        
        return currentSubscriptionInfo;
    }
    
    public void updateAssignments() throws Exception
    {        
        setDefault(subscriptionDao.queryForEq(Subscription.DEFAULT, true).get(0));
    }
    
    public void updateOrgSubscriptionAssignment(long customerId) throws SQLException
    {
        CustomerSubscriptionAssignment orgSubscriptionAssigment = customerSubscriptionAssignmentDao.
            queryBuilder().where().eq(CustomerSubscriptionAssignment.CUSTOMER_ID, customerId).queryForFirst();
        
        subscriptionDao.refresh(orgSubscriptionAssigment.subscription);
        //organizationDao.refresh(orgSubscriptionAssigment.organization);
        
        List<ChargeableService> chargeableServices = chargeableServiceDao.queryBuilder().where()
                .eq(ChargeableService.TYPE, ChargeableServiceType.SUBSCRIPTION.getValue()).query();

        List<Long> chargeableServiceIds = new ArrayList<>();

        chargeableServices.stream().forEach((chargeableService) -> {
            chargeableServiceIds.add(chargeableService.id);
        });

        Payment payment = null;
        
        if(chargeableServiceIds.size() > 0){
            QueryBuilder<Payment, Long> paymentQB = paymentDao.queryBuilder();
            Where<Payment, Long> paymentWH = paymentQB.where();

            paymentWH.eq(Payment.CUSTOMER_ID, orgSubscriptionAssigment.customerId);
            paymentWH.and();
            paymentWH.in(Payment.CHARGEABLE_SERVICE_ID, chargeableServiceIds);
            paymentWH.and();
            paymentWH.gt(Payment.END_TIME, new Date());
            paymentQB.orderBy(Payment.START_TIME, true);

            payment = paymentQB.queryForFirst();
        }        

        if(payment == null){
            orgSubscriptionAssigment.subscription = subscriptionDao.queryBuilder().where()
                .eq(Subscription.DEFAULT, true).queryForFirst();
            orgSubscriptionAssigment.endTime = new Date(MAX_DATE);
        }
        else
        {
            chargeableServiceDao.refresh(payment.chargeableService);
            orgSubscriptionAssigment.subscription = subscriptionDao.queryBuilder().where()
                .eq(Subscription.ID, payment.chargeableService.referenceId).queryForFirst();
            orgSubscriptionAssigment.startTime = payment.startTime;
            orgSubscriptionAssigment.endTime = payment.endTime;
        }

        customerSubscriptionAssignmentDao.update(orgSubscriptionAssigment);
    }
}