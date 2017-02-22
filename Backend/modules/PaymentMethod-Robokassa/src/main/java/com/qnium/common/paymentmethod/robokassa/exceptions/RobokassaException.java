/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.paymentmethod.robokassa.exceptions;

/**
 *
 * @author Administrator
 */
public class RobokassaException extends Exception {
    
    public RobokassaException(String message){
        super(message);
    }
    
    public RobokassaException(String message, Throwable cause){
        super(message, cause);
    }
}
