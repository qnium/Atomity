/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.atomitybackend.auth;

import com.qnium.atomitybackend.auth.exceptions.AuthorizationException;
import com.qnium.atomitybackend.auth.definitions.AccountType;
import com.qnium.atomitybackend.common.definitions.ErrorCodes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Kirill Zhukov
 */
public class AuthorizationManager {
    
    private final ArrayList<PermissionRecord> _permissions;
    
    private class PermissionRecord{

        public PermissionRecord(AccountType account, String action, String role) {
            this.account = account;
            this.action = action;
            this.role = role;
        }
        AccountType account;
        String action;
        String role;
    }
    
    private AuthorizationManager() {
        this._permissions = new ArrayList();
    }
    
    public static AuthorizationManager getInstance() {
        return AuthorizationManagerHolder.INSTANCE;
    }
    
    private static class AuthorizationManagerHolder {

        private static final AuthorizationManager INSTANCE = new AuthorizationManager();
    }
    
    public void addPermission(AccountType type, String action, String Role)
    {
        _permissions.add(new PermissionRecord(type, action, Role));
    }

    public void addPermission(AccountType type, String action)
    {
        addPermission(type, action, null);
    }
    
    public void checkPermission(AccountType type, String action, String Role) throws AuthorizationException
    {
        try{
            List<PermissionRecord> query = _permissions.stream().filter( p -> p.action.equals(action) ).collect(Collectors.toList());
            
            if (!query.isEmpty()) //Action has restricted access, permission should be verified
            {
                
                //Check access in context of current application (account type)
                List<PermissionRecord> actions = query.stream().filter( p -> p.account.equals(type)).collect(Collectors.toList());
                
                if (!actions.isEmpty()) // Access restricted to current account type
                {
                    if (!actions.stream().filter( p-> p.role == null || p.role.equals(Role) ).findAny().isPresent())
                        throw new AuthorizationException(ErrorCodes.ACCESS_DENIED, "Permission not found");
                } else {
                    throw new AuthorizationException(ErrorCodes.ACCESS_DENIED, "Access is not allowed for this type of account");
                }
            }
        } catch(Exception ex)
        {
            throw new AuthorizationException(ErrorCodes.ACCESS_DENIED, "Permission denied");
        }
    }
}
