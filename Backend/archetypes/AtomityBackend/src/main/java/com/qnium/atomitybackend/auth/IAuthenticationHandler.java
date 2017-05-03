/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.atomitybackend.auth;

import com.qnium.atomitybackend.auth.exceptions.AuthenticationException;

/**
 *
 * @author Kirill Zhukov
 */
public interface IAuthenticationHandler {

    /**
     *
     * @param login
     * @param password
     * @return long Authenticated user Id
     * @throws AuthenticationException
     */
    public SessionContext authenticate(String login, String password) throws AuthenticationException;
    
}
