/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.paymentsystem.dataobjects;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Date;

/**
 *
 * @author
 */
@DatabaseTable(tableName = "payments")
public class Payment {
    
    public static final String ID = "id";
    @DatabaseField(columnName = ID, generatedId = true)
    public long id;
    
    public static final String PAYMENT_TIME = "payment_time";
    @DatabaseField(columnName = PAYMENT_TIME)
    public Date paymentTime;
    
    public static final String AMOUNT = "amount";
    @DatabaseField(columnName = AMOUNT)
    public double amount;
    
    public static final String CUSTOMER_ID = "customer_id";
    @DatabaseField(columnName = CUSTOMER_ID/*, foreign = true*/)
    public long customerId;
    
    public static final String CHARGEABLE_SERVICE_ID = "chargeable_service_id";
    @DatabaseField(columnName = CHARGEABLE_SERVICE_ID, foreign = true)
    public ChargeableService chargeableService;
    
    public static final String START_TIME = "start_time";
    @DatabaseField(columnName = START_TIME)
    public Date startTime;
    
    public static final String END_TIME = "end_time";
    @DatabaseField(columnName = END_TIME)
    public Date endTime;
    
    public static final String PAYMENT_CONSUMED = "payment_consumed";
    @DatabaseField(columnName = PAYMENT_CONSUMED)
    public boolean paymentConsumed;
    
    public static final String ORDER_ID = "order_id";
    @DatabaseField(columnName = ORDER_ID, foreign = true)
    public Order order;
    
    public Customer customer;    
}
