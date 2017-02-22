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
public enum ChargeableServiceDurationType {
    
    DAYS (1),
    MONTHS (2);
    
    private final int durationType;
    
    private ChargeableServiceDurationType(int durationType){
        this.durationType = durationType;
    }
    
    public int getValue(){
        return durationType;
    }
}
