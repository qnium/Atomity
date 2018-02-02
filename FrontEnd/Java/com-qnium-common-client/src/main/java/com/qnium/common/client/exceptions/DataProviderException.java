/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.client.exceptions;

import com.qnium.common.definitions.ErrorCode;

/**
 *
 * @author Drozhin
 */
public class DataProviderException extends Exception {
    private final ErrorCode errorCode;
    
    public DataProviderException() {
        this(ErrorCode.GENERAL_ERROR);
    }

    public DataProviderException(ErrorCode errorCode) {
        this(errorCode, (Exception)null);
    }

    public DataProviderException(ErrorCode errorCode, String errorMessage) {
        this(errorCode, errorMessage, null);
    }
    
    public DataProviderException(ErrorCode errorCode, Exception innerException) {
        this(errorCode, "", innerException);
    }   

    public DataProviderException(ErrorCode errorCode, String errorMessage, Exception innerException) {
        super(errorMessage, innerException);
        this.errorCode = errorCode;
    }   

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
