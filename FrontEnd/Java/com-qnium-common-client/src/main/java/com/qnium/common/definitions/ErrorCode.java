/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.definitions;

/**
 *
 * @author Drozhin
 */
public enum ErrorCode {
    GENERAL_ERROR (1),
    CONNECTION_ERROR (2),
    API_URL_ERROR (3),
    SEND_REQUEST_ERROR (4),
    GET_RESPONSE_CODE_ERROR (5),
    INCORRECT_RESPONSE_CODE (6),
    GET_RESPONSE_ERROR (7),
    PROCESS_RESPONSE_ERROR (8);
    
    private final int value;
    
    private ErrorCode(int errorCode){
        this.value = errorCode;
    }
    
    public int getValue(){
        return value;
    }
}
