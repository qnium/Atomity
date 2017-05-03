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
public class AuthenticationException extends CommonException {
    public static final int AUTHENTICATION_ERROR = 0xAA000001;
    public AuthenticationException(int errorCode, String message){
        super(errorCode, message);
    }
}
