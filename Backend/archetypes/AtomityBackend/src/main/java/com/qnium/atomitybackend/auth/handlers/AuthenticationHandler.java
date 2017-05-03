/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.atomitybackend.auth.handlers;

import com.qnium.atomitybackend.auth.AuthenticationManager;
import com.qnium.atomitybackend.auth.SessionContext;
import com.qnium.atomitybackend.auth.definitions.AccountType;
import com.qnium.atomitybackend.auth.exceptions.AuthenticationException;
import com.qnium.common.backend.assets.dataobjects.ObjectResponseMessage;
import com.qnium.common.backend.assets.dataobjects.RequestMessage;
import com.qnium.common.backend.assets.interfaces.IHandler;
import com.qnium.common.backend.exceptions.CommonException;
import java.io.IOException;

/**
 *
 * @author Kirill Zhukov
 */
public class AuthenticationHandler implements IHandler<RequestMessage<AuthenticationRequest>, ObjectResponseMessage<String>>
{    
    
    public AuthenticationHandler(){
    }

    @Override
    public ObjectResponseMessage<String> process(RequestMessage<AuthenticationRequest> request) throws IOException, CommonException {
        ObjectResponseMessage<String> resp = new ObjectResponseMessage<>();
        
        try{
            if(request.data.login == null) request.data.login = "";
            if(request.data.password == null) request.data.password = "";
            AccountType type = AccountType.valueOf(request.data.accountType);
            SessionContext authentication = AuthenticationManager.getInstance().getHandlerByAccountType(type).authenticate(request.data.login, request.data.password);
            authentication.setAccountType(type);
            String sessionKey = authentication.getSessionKey();
            resp.result = sessionKey;
        }
        catch (AuthenticationException ex){
            throw new CommonException(ex.getErrorCode(), ex.getMessage());
        }        
        catch (Exception ex) {
            resp.error = ex.getMessage();
        }
        
        return resp;
    }

    
    
}
