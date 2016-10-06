/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.paymentsystem.definitions;

/**
 *
 * @author Drozhin
 */
public enum ChargeableServiceType {
    
    SUBSCRIPTION (1),
    ONE_TIME_SERVICE (2);
    
    private final int serviceType_id;
    
    private ChargeableServiceType(int serviceType){
        serviceType_id = serviceType;
    }
    
    public int getValue(){
        return serviceType_id;
    }
}
