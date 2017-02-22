/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.paymentsystem.handlers.subscriptions;

import com.qnium.common.backend.assets.dataobjects.CountResponseMessage;
import com.qnium.common.backend.assets.dataobjects.RequestMessage;
import com.qnium.common.backend.assets.dataobjects.UpdateRequestParameters;
import com.qnium.common.backend.assets.interfaces.IHandler;
import com.qnium.common.backend.core.AuthManager;
import com.qnium.common.paymentsystem.dao.SubscriptionManager;
import com.qnium.common.paymentsystem.dataobjects.Subscription;
import java.io.IOException;

/**
 *
 * @author
 */
public class SetDefaultSubscriptionHandler implements IHandler{

    @Override
    public Object process(Object request) throws IOException {
        try {
            RequestMessage<UpdateRequestParameters<Subscription>> req = (RequestMessage<UpdateRequestParameters<Subscription>>)request;
            
            AuthManager.getInstance().verifyAdminPrivileges(req.data.sessionKey);
            
            SubscriptionManager sm = SubscriptionManager.getInstance();
            sm.setDefault(req.data.entities.get(0));
            return new CountResponseMessage(sm.getCount());
        }
        catch (Exception ex) {
            throw new IOException(ex);
        }
    }
    
}
