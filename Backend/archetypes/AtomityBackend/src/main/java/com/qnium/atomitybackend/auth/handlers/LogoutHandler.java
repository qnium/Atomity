/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.atomitybackend.auth.handlers;

import com.qnium.common.backend.assets.dataobjects.RequestMessage;
import com.qnium.common.backend.assets.dataobjects.ResponseMessage;
import com.qnium.common.backend.assets.interfaces.IHandler;
import com.qnium.atomitybackend.auth.SessionManager;
import java.io.IOException;

/**
 *
 * @author Drozhin
 */
public class LogoutHandler implements IHandler<RequestMessage, ResponseMessage>
{
    @Override
    public ResponseMessage process(RequestMessage request) throws IOException
    {
        ResponseMessage resp = new ResponseMessage();
        
        try{
            SessionManager.getInstance().removeSession(request.sessionKey);
        } catch (Exception ex) {
            resp.error = "Low level error.";
        }
        
        return resp;
    }    
}
