/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.paymentsystem.dataobjects;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 *
 * @author Drozhin
 */
@DatabaseTable(tableName = "order_service_assignments")
public class OrderChargeableServiceAssignment {
    public static final String ID = "id";
    @DatabaseField(columnName = ID, generatedId = true)
    public long id;
    
    public static final String ORDER_ID = "order_id";
    @DatabaseField(columnName = ORDER_ID, foreign = true)
    public Order order;
    
    public static final String CHARGEABLE_SERVICE_ID = "chargeable_service_id";
    @DatabaseField(columnName = CHARGEABLE_SERVICE_ID, foreign = true)
    public ChargeableService chargeableService;
    
    public static final String ROW_NUMBER = "row_number";
    @DatabaseField(columnName = ROW_NUMBER)
    public int rowNumber;
}
