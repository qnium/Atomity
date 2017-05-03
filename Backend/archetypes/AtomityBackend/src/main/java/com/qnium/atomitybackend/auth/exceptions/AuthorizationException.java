/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.atomitybackend.auth.exceptions;

import com.qnium.common.backend.exceptions.CommonException;

/**
 *
 * @author nbv
 */
public class AuthorizationException extends CommonException {
    
    public static int ERROR_ACCESS_DENIED = -178;
    
    public AuthorizationException(int errorCode, String message){
        super(errorCode, message);
    }
}
