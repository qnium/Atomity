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
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.qnium.common.paymentsystem.dataobjects.Payment;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import com.qnium.common.backend.assets.definitions.FieldOperations;
import com.qnium.common.backend.assets.interfaces.IEntityManager;
import com.qnium.common.backend.core.ConfigManager;
import com.qnium.common.backend.core.FieldFilter;
import com.qnium.common.backend.core.FilteredResult;
import com.qnium.common.paymentsystem.core.PaymentSystem;
import com.qnium.common.paymentsystem.dataobjects.ChargeableService;
import com.qnium.common.paymentsystem.dataobjects.Customer;
import com.qnium.common.paymentsystem.dataobjects.CustomerSubscriptionAssignment;
import com.qnium.common.paymentsystem.dataobjects.PaymentInfo;
import com.qnium.common.paymentsystem.dataobjects.Subscription;
import com.qnium.common.paymentsystem.definitions.ChargeableServiceDurationType;
import com.qnium.common.paymentsystem.definitions.ChargeableServiceType;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author
 */
public class PaymentManager implements IEntityManager<Payment> {

    private static PaymentManager instance;
    
    private final String ORGANIZATION_NAME_FILTER = "organization_name";
    
    private final Dao<Payment, Long> paymentDao;
    private final Dao<ChargeableService, Long> chargeableServiceDao;
    private final Dao<CustomerSubscriptionAssignment, Long> orgSubscriptionAssignmentDao;
    private final Dao<Subscription, Long> subscriptionDao;
    
    public static synchronized PaymentManager getInstance() throws SQLException {
        if (instance == null) {
            instance = new PaymentManager();
        }
        return instance;
    }
    
    private PaymentManager() throws SQLException
    {
        ConnectionSource connectionSource = new JdbcConnectionSource(ConfigManager.getInstance().getDatabaseURL());
        
        paymentDao = DaoManager.createDao(connectionSource, Payment.class);
        if (!paymentDao.isTableExists()) {
            TableUtils.createTable(connectionSource, Payment.class);
        }

        chargeableServiceDao = DaoManager.createDao(connectionSource, ChargeableService.class);
        if (!chargeableServiceDao.isTableExists()) {
            TableUtils.createTable(connectionSource, ChargeableService.class);
        }

        orgSubscriptionAssignmentDao = DaoManager.createDao(connectionSource, CustomerSubscriptionAssignment.class);
        if (!orgSubscriptionAssignmentDao.isTableExists()) {
            TableUtils.createTable(connectionSource, CustomerSubscriptionAssignment.class);
        }
        
        subscriptionDao = DaoManager.createDao(connectionSource, Subscription.class);
        if (!subscriptionDao.isTableExists()) {
            TableUtils.createTable(connectionSource, Subscription.class);
        }
    }
    
    @Override
    public void create(Payment entity) throws Exception
    {
        chargeableServiceDao.refresh(entity.chargeableService);
        if(entity.chargeableService.type == ChargeableServiceType.ONE_TIME_SERVICE.getValue())
        {
            entity.startTime = new Date();
            entity.endTime = new Date();
            paymentDao.create(entity);
        }
        else
        {
            entity.endTime = calcEndDate(entity);
            if(entity.endTime.before(entity.startTime)){
               throw new Exception("INVALID_END_DATE");
            }
            if(paymentHaveCrossPeriod(entity)){
                throw new Exception("INVALID_PERIOD_OF_ACTIVITY");
            }
            paymentDao.create(entity);

            SubscriptionManager.getInstance().updateOrgSubscriptionAssignment(entity.customerId);
        }
    }
    
//    private void addOrganizationNameCondition(Where<Payment, Long> paymentWhere, String orgName) 
//            throws SQLException {
//        List<Organization> orgs = organizationDao.queryBuilder()
//            .where()
//            .like(Organization.ORGANIZATION_NAME, "%" + orgName + "%")
//            .query();
//        List<Long> orgIds = new ArrayList<>();
//        for (Organization org : orgs) {
//            orgIds.add(org.id);
//        }
//        paymentWhere.and().in(Payment.CUSTOMER_ID, orgIds);
//    }

    @Override
    public FilteredResult<Payment> read(List<FieldFilter> filters, long startIndex, long count) throws Exception {
        QueryBuilder<Payment, Long> paymentQB = paymentDao.queryBuilder();
        paymentQB.orderBy(Payment.PAYMENT_TIME, false);
        if (filters != null && filters.size() > 0) {
            Where<Payment, Long> paymentWhere = paymentQB.where();
            paymentWhere.isNotNull(Payment.ID);
            Date dateToCompare;
            for (FieldFilter f : filters) {
//                if (f.field.equals(ORGANIZATION_NAME_FILTER)) {
//                    if (f.value != null && !f.value.toString().isEmpty()) {
//                        addOrganizationNameCondition(paymentWhere, f.value.toString());
//                    }
//                }
//                else 
                    if (f.value != null && !f.value.toString().isEmpty()) {
                    paymentWhere.and();
                    switch (f.operation) {
                        case FieldOperations.EQ:
                            paymentWhere.eq(f.field, f.value);
                            break;
                        case FieldOperations.LIKE:
                            paymentWhere.like(f.field, "%" + f.value + "%");
                            break;
                        case FieldOperations.DATE_LE:
                            dateToCompare = new Date();
                            dateToCompare.setTime((Long)f.value);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(dateToCompare);
                            calendar.add(Calendar.DATE, 1);
                            calendar.add(Calendar.SECOND, -1);
                            dateToCompare = calendar.getTime();
                            paymentWhere.le(f.field, dateToCompare);
                            break;
                        case FieldOperations.DATE_GE:
                            dateToCompare = new Date();
                            dateToCompare.setTime((Long)f.value);
                            paymentWhere.ge(f.field, dateToCompare);
                            break;
                    }
                }
            }
        }
        FilteredResult<Payment> result = new FilteredResult<>();
        result.totalCounter = paymentQB.countOf();
        paymentQB.setCountOf(false);

        if (count > 0) {
            paymentQB = paymentQB.offset(startIndex).limit(count);
        }
        
        List<Payment> payments = paymentQB.query();
        
        List<Long> customerIds = 
                payments.stream()
                .map(p -> p.customerId)
                .collect(Collectors.toList());
        
        List<Customer> customers = PaymentSystem.getProvider().getCustomers(customerIds);//.stream().distinct().collect(Collectors.toList());
        
        payments.forEach(payment -> payment.customer = 
            customers.stream()
            .filter(customer -> customer.id == payment.customerId)
            .findAny()
            .orElse(null));
        
        for(Payment payment : payments) {
            chargeableServiceDao.refresh(payment.chargeableService);
        }
        
        result.data = payments;
        
        return result;
    }

    @Override
    public void update(List<Payment> entities) throws Exception
    {
        for( Payment payment: entities)
        {
            if(payment.chargeableService.type == ChargeableServiceType.ONE_TIME_SERVICE.getValue())
            {
                payment.startTime = new Date();
                payment.endTime = new Date();
                paymentDao.update(payment);
            }
            else
            {            
                payment.endTime = calcEndDate(payment);
                if(payment.endTime.before(payment.startTime)){
                   throw new Exception("INVALID_END_DATE");
                }
                if(paymentHaveCrossPeriod(payment)){
                   throw new Exception("INVALID_PERIOD_OF_ACTIVITY");
                }

                Payment paymentBeforeUpdate = paymentDao.queryBuilder().where().eq(Payment.ID, payment.id).queryForFirst();

                paymentDao.update(payment);

                if(paymentBeforeUpdate.customerId != payment.customerId){
                    SubscriptionManager.getInstance().updateOrgSubscriptionAssignment(paymentBeforeUpdate.customerId);
                }
                SubscriptionManager.getInstance().updateOrgSubscriptionAssignment(payment.customerId);
            }
        }
    }

    @Override
    public void delete(List<Payment> entities) throws Exception {
        for( Payment payment: entities){
            paymentDao.delete(payment);
            SubscriptionManager.getInstance().updateOrgSubscriptionAssignment(payment.customerId);
        }
    }

    @Override
    public long getCount() throws Exception {
        return paymentDao.countOf();
    }
    
    public PaymentInfo calcPaymentParameters(Payment payment) throws Exception {
        
        PaymentInfo paymentInfo = new PaymentInfo();
        
        paymentInfo.endTime = calcEndDate(payment);
        
        if(payment.chargeableService != null && payment.chargeableService.id > 0){
            chargeableServiceDao.refresh(payment.chargeableService);
            paymentInfo.amount = payment.chargeableService.price;
        }
        
        return paymentInfo;
    }
    
    public Date calcEndDate(Payment payment) throws Exception {
        
        Date calulatedEndDate = null;
        
        if(payment.chargeableService != null && payment.chargeableService.id != 0 && payment.startTime != null){
            
            Calendar calendar = Calendar.getInstance();//(Locale.ROOT);
            calendar.setTime(payment.startTime);

            chargeableServiceDao.refresh(payment.chargeableService);
            
            if(payment.chargeableService.durationType == ChargeableServiceDurationType.DAYS.getValue()){
                calendar.add(Calendar.DATE, payment.chargeableService.durationOfActivity);
            }

            if(payment.chargeableService.durationType == ChargeableServiceDurationType.MONTHS.getValue()){
                calendar.add(Calendar.MONTH, payment.chargeableService.durationOfActivity);
            }
            
            calulatedEndDate = calendar.getTime();
        }
        
        return calulatedEndDate;
    }
    
    public boolean paymentHaveCrossPeriod(Payment payment) throws Exception {
        
        QueryBuilder<Payment, Long> paymentQB = paymentDao.queryBuilder();
        Where<Payment, Long> where = paymentQB.where();
        
        where.ge(Payment.END_TIME, payment.startTime);
        where.and();
        where.le(Payment.START_TIME, payment.endTime);
        where.and();
        where.ne(Payment.ID, payment.id);
        where.and();
        where.eq(Payment.CUSTOMER_ID, payment.customerId);
        where.and();
        where.eq(Payment.CHARGEABLE_SERVICE_ID, payment.chargeableService.id);
        
        return paymentQB.countOf() > 0;
    }
}
