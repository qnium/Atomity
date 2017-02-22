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
@DatabaseTable(tableName = "subscription_feature_assignment")
public class SubscriptionFeatureAssignment {
    public static final String ID = "id";
    @DatabaseField(columnName = ID, generatedId = true)
    public long id;
    
    public static final String SUBSCRIPTION_ID = "subscription_id";
    @DatabaseField(columnName = SUBSCRIPTION_ID, foreign = true)
    public Subscription subscription;
    
    public static final String FEATURE_ID = "feature_id";
    @DatabaseField(columnName = FEATURE_ID)
    public long featureId;
    
    public Feature feature;
}
