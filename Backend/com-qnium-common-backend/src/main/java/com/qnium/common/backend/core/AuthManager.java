/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.backend.core;

import com.qnium.common.backend.assets.interfaces.IAuthManagerProvider;

/**
 *
 * @author Drozhin
 */
public class AuthManager
{
    private static AuthManager _instance = null;
    
    IAuthManagerProvider authManagerProvider = new DefaultAuthManagerProvider();
    
    public static synchronized AuthManager getInstance()
    {
        if ( _instance == null )
        {
            _instance = new AuthManager();
        }
        
        return _instance;
    }
    
    private AuthManager() {        
    }
    
    public void setAuthManagerProvider(IAuthManagerProvider authManagerProvider)
    {
        this.authManagerProvider = authManagerProvider;        
    }
    
    /*
    public void verifyAdminPrivileges(String sessionKey) throws Exception
    {
        authManagerProvider.verifyAdminPrivileges(sessionKey);
    }
    */
    
    public void checkPermission(String sessionKey, String entity, String action) throws Exception
    {
        this.authManagerProvider.checkPermission(sessionKey, entity, action);
    }
    
    public long getAccountId(String sessionKey) throws Exception
    {
        return this.authManagerProvider.getAccountId(sessionKey);
    }
    
    public int getAccountType(String sessionKey) throws Exception
    {
        return this.authManagerProvider.getAccountType(sessionKey);
    }
}
