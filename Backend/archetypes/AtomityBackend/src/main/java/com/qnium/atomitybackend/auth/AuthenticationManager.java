/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.atomitybackend.auth;

import com.qnium.atomitybackend.auth.definitions.AccountType;
import com.qnium.atomitybackend.auth.exceptions.AuthenticationException;
import com.qnium.atomitybackend.common.definitions.ErrorCodes;
import java.util.ArrayList;
import java.util.Optional;


public class AuthenticationManager {
    
    private class HandlerRecord{

        public HandlerRecord(AccountType accountType, IAuthenticationHandler handler) {
            this.accountType = accountType;
            this.handler = handler;
        }
        
        
        public AccountType accountType;
        public IAuthenticationHandler handler;
    }
    
    ArrayList<HandlerRecord> _handlers;
    
    private AuthenticationManager() {
        this._handlers = new ArrayList<>();
    }
    
    public IAuthenticationHandler getHandlerByAccountType(AccountType type) throws AuthenticationException
    {
        Optional<HandlerRecord> found = this._handlers.stream().filter( t -> t.accountType == type ).findAny();
        if (found.isPresent())
            return found.get().handler;
        else throw new AuthenticationException(ErrorCodes.GENERAL_ERROR, "No authentication handler for such application type");
    }
    
    public static AuthenticationManager getInstance() {
        return AuthenticationManagerHolder.INSTANCE;
    }
    
    private static class AuthenticationManagerHolder {

        private static final AuthenticationManager INSTANCE = new AuthenticationManager();
    }
    
    public void addAuthenticationHandler(AccountType type, IAuthenticationHandler handler)
    {
        _handlers.add(new HandlerRecord(type, handler));
    }
}
