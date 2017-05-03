/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.atomitybackend.auth.handlers;

import com.qnium.common.backend.assets.dataobjects.ObjectResponseMessage;
import com.qnium.common.backend.assets.dataobjects.RequestMessage;
import com.qnium.common.backend.assets.interfaces.IHandler;
import com.qnium.atomitybackend.auth.SessionManager;
import com.qnium.atomitybackend.auth.definitions.AccountType;
import java.io.IOException;

/**
 *
 * @author Drozhin
 */
public class CheckExistsSessionHandler implements IHandler<RequestMessage, ObjectResponseMessage<Boolean>>
{
    @Override
    public ObjectResponseMessage<Boolean> process(RequestMessage request) throws IOException
    {
        ObjectResponseMessage<Boolean> resp = new ObjectResponseMessage();
        
        try {
            resp.result = SessionManager.getInstance().getSession(request.sessionKey).getAccountType() != AccountType.NO_SESSION;
        } catch (Exception ex) {
            resp.error = "Low level error.";
        }
        
        return resp;
    }
}
