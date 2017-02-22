/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.paymentsystem.dao;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.qnium.common.backend.core.ConfigManager;
import com.qnium.common.paymentsystem.core.PaymentSystem;
import com.qnium.common.paymentsystem.dataobjects.ChargeableService;
import com.qnium.common.paymentsystem.dataobjects.Feature;
import com.qnium.common.paymentsystem.dataobjects.Payment;
import com.qnium.common.paymentsystem.dataobjects.Subscription;
import com.qnium.common.paymentsystem.dataobjects.SubscriptionFeatureAssignment;
import com.qnium.common.paymentsystem.definitions.ChargeableServiceType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author
 */
public class FeatureManager
{    
    private static FeatureManager instance;
    
    private final Dao<Payment, Long> paymentDao;
    private final Dao<ChargeableService, Long> chargeableServiceDao;
    private final Dao<SubscriptionFeatureAssignment, Long> subscriptionServiceAssignmentDao;
        
    public static synchronized FeatureManager getInstance() throws SQLException {
        if (instance == null) {
            instance = new FeatureManager();
        }
        return instance;
    }
    
    private FeatureManager() throws SQLException {
        String databaseUrl = ConfigManager.getInstance().getDatabaseURL();
        ConnectionSource connectionSource = new JdbcConnectionSource(databaseUrl);
        
        paymentDao = DaoManager.createDao(connectionSource, Payment.class);
        if (!paymentDao.isTableExists()) {
            TableUtils.createTable(connectionSource, Payment.class);
        }
        
        subscriptionServiceAssignmentDao = DaoManager.createDao(connectionSource, SubscriptionFeatureAssignment.class);
        if (!subscriptionServiceAssignmentDao.isTableExists()) {
            TableUtils.createTable(connectionSource, SubscriptionFeatureAssignment.class);
        }
        
        chargeableServiceDao = DaoManager.createDao(connectionSource, ChargeableService.class);
        if (!chargeableServiceDao.isTableExists()) {
            TableUtils.createTable(connectionSource, ChargeableService.class);
        }
    }

    private List<Long> getAccessibleFeatureIdsFromSubscriptions(long customerId) throws SQLException
    {
        Subscription currentSubscription = SubscriptionManager.getInstance().getCurrentSubscription(customerId).subscription;
        
        List<SubscriptionFeatureAssignment> assignments = subscriptionServiceAssignmentDao.queryBuilder().where()
            .eq(SubscriptionFeatureAssignment.SUBSCRIPTION_ID, currentSubscription.id).query();
                
        List<Long> featureIds = new ArrayList<>();
        
        assignments.stream().forEach((assignment) -> {
            featureIds.add(assignment.featureId);
        });
        
        return featureIds;
    }
    
    private List<Long> getAccessibleFeatureIdsFromOneTimeServices(long customerId) throws SQLException
    {
        QueryBuilder<Payment, Long> customerPaymentsQB = paymentDao.queryBuilder();
        customerPaymentsQB.selectColumns(Payment.CHARGEABLE_SERVICE_ID);
        customerPaymentsQB.where()
                .eq(Payment.CUSTOMER_ID, customerId)
                .and()
                .eq(Payment.PAYMENT_CONSUMED, false);        

        List<Long> paidFeatureIds = chargeableServiceDao.queryBuilder().where()
              .in(ChargeableService.ID, customerPaymentsQB)
              .and()
              .eq(ChargeableService.TYPE, ChargeableServiceType.ONE_TIME_SERVICE.getValue())
              .query().stream().map(m->m.referenceId).collect(Collectors.toList());
        
        return paidFeatureIds;
    }
    
    public Feature[] getAccessibleFeatures(long customerId) throws SQLException, Exception
    {        
        List<Long> featureIds = getAccessibleFeatureIdsFromSubscriptions(customerId);
        
        featureIds.addAll(getAccessibleFeatureIdsFromOneTimeServices(customerId));
        
        Feature[] features = PaymentSystem.getProvider().getFeatures(featureIds).toArray(new Feature[0]);
        
        return features;
    }
    
    public void featureConsume(long customerId, long featureId) throws SQLException
    {
        if(!getAccessibleFeatureIdsFromSubscriptions(customerId).contains(featureId))
        {
            QueryBuilder<ChargeableService, Long> chargeableServicesQB = chargeableServiceDao.queryBuilder();
            chargeableServicesQB.selectColumns(ChargeableService.ID);
            chargeableServicesQB.where()
                    .eq(ChargeableService.TYPE, ChargeableServiceType.ONE_TIME_SERVICE.getValue())
                    .and()
                    .eq(ChargeableService.REFERENCE_ID, featureId);
            
            Payment payment = paymentDao.queryBuilder().where()
                .eq(Payment.CUSTOMER_ID, customerId)
                .and()
                .eq(Payment.PAYMENT_CONSUMED, false)
                .and()
                .in(Payment.CHARGEABLE_SERVICE_ID, chargeableServicesQB)
                .queryForFirst();
            
            payment.paymentConsumed = true;
            
            paymentDao.update(payment);
        }
    }
}
