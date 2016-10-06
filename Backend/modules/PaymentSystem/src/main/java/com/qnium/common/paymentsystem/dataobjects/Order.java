/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.paymentsystem.dataobjects;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Drozhin
 */
@DatabaseTable(tableName = "orders")
public class Order {
    public static final String ID = "id";
    @DatabaseField(columnName = ID, generatedId = true)
    public long id;
    
    public static final String CUSTOMER_ID = "customer_id";
    @DatabaseField(columnName = CUSTOMER_ID)
    public long customerId;
    
    public static final String CREATION_DATE = "creation_date";
    @DatabaseField(columnName = CREATION_DATE)
    public Date creationDate;

    public static final String AMOUNT = "amount";
    @DatabaseField(columnName = AMOUNT)
    public double amount;
    
    public static final String PAID = "paid";
    @DatabaseField(columnName = PAID)
    public boolean paid;
    
    public List<ChargeableService> chargeableServices;
    public List<Payment> payments;
}
