/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.atomitybackend.auth;

import com.qnium.atomitybackend.auth.definitions.AccountType;

/**
 *
 * @author nbv
 */
public class SessionContext {
    private AccountType _accountType;
    private String _role;
    private String _sessionKey;
    private long _userId;

    public SessionContext()
    {
        this._accountType = AccountType.NO_SESSION;
        _userId = -1;
    }
    
    public AccountType getAccountType() {
        return _accountType;
    }

    public void setAccountType(AccountType _accountType) {
        this._accountType = _accountType;
    }

    public String getRole() {
        return _role;
    }

    public void setRole(String _role) {
        this._role = _role;
    }

    public String getSessionKey() {
        return _sessionKey;
    }

    public void setSessionKey(String _sessionKey) {
        this._sessionKey = _sessionKey;
    }

    public long getUserId() {
        return _userId;
    }

    public void setUserId(long _userId) {
        this._userId = _userId;
    }
}
