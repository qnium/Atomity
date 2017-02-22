/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.backend.exceptions;

/**
 *
 * @author Drozhin
 */
public class CommonException extends Exception
{
    public static int VALIDATION_ERROR = -177;
    
    private final int errorCode;
    
    public int getErrorCode(){
        return errorCode;
    }
    
    public CommonException(int errorCode, String message){
        this(errorCode, message, null);
    }
    
    public CommonException(int errorCode, String message, Throwable cause){
        super(message, cause);
        this.errorCode = errorCode;
    }
}
