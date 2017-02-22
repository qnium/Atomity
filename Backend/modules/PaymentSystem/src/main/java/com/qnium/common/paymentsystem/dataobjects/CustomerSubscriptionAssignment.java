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
@DatabaseTable(tableName = "customer_subscription_assignment")
public class CustomerSubscriptionAssignment {
    public static final String ID = "id";
    @DatabaseField(columnName = ID, generatedId = true)
    public long id;
    
    public static final String CUSTOMER_ID = "customer_id";
    @DatabaseField(columnName = CUSTOMER_ID)
    public long customerId;
    
    public static final String SUBSCRIPTION_ID = "subscription_id";
    @DatabaseField(columnName = SUBSCRIPTION_ID, foreign = true)
    public Subscription subscription;
    
    public static final String START_TIME = "start_time";
    @DatabaseField(columnName = START_TIME)
    public Date startTime;

    public static final String END_TIME = "end_time";
    @DatabaseField(columnName = END_TIME)
    public Date endTime;
   
    public Customer customer;
}
