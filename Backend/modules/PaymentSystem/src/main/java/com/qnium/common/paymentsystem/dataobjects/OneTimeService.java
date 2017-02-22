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
@DatabaseTable(tableName = "one_time_services")
public class OneTimeService
{
    public static final String ID = "id";
    @DatabaseField(columnName = ID, generatedId = true)
    public long id;
    
    public static final String NAME = "name";
    @DatabaseField(columnName = NAME)
    public String name;
    
    public static final String FEATURE_ID = "feature_id";
    @DatabaseField(columnName = FEATURE_ID)
    public long featureId;
    
    public Feature feature;
}
