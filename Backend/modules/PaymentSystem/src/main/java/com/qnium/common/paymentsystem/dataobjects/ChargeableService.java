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
 * @author
 */
@DatabaseTable(tableName = "chargeable_services")
public class ChargeableService {
    
    public static final String ID = "id";
    @DatabaseField(columnName = ID, generatedId = true)
    public long id;
    
    public static final String NAME = "name";
    @DatabaseField(columnName = NAME)
    public String name;
    
    public static final String DESCRIPTION = "description";
    @DatabaseField(columnName = DESCRIPTION)
    public String description;
    
    public static final String TYPE = "type";
    @DatabaseField(columnName = TYPE)
    public int type;
    
    public static final String REFERENCE_ID = "reference_id";
    @DatabaseField(columnName = REFERENCE_ID)
    public long referenceId;
    
    public static final String PRICE = "price";
    @DatabaseField(columnName = PRICE)
    public double price;
    
    public static final String DURATION_OF_ACTIVITY = "duration_of_activity";
    @DatabaseField(columnName = DURATION_OF_ACTIVITY)
    public int durationOfActivity;
    
    public static final String DURATION_TYPE = "duration_type";
    @DatabaseField(columnName = DURATION_TYPE)
    public int durationType;
}
