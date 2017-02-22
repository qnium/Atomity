/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.paymentsystem.handlers.subscriptions;

import com.qnium.common.backend.assets.dataobjects.CollectionResponseMessage;
import com.qnium.common.backend.assets.dataobjects.RequestMessage;
import com.qnium.common.backend.assets.dataobjects.ByIdRequestParameters;
import com.qnium.common.backend.assets.interfaces.IHandler;
import com.qnium.common.backend.core.AuthManager;
import com.qnium.common.paymentsystem.dao.SubscriptionManager;
import com.qnium.common.paymentsystem.dataobjects.Feature;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author
 */
public class GetFeaturesBySubscriptionIdHandler implements IHandler{

    @Override
    public Object process(Object request) throws IOException {
        try{
            RequestMessage<ByIdRequestParameters> req = (RequestMessage<ByIdRequestParameters>)request;
            
            AuthManager.getInstance().verifyAdminPrivileges(req.data.sessionKey);
                      
            SubscriptionManager sm = SubscriptionManager.getInstance();
            Feature[] result = sm.getFeaturesBySubscriptionId(req.data.id);
            CollectionResponseMessage response = new CollectionResponseMessage();
            response.totalCounter = result.length;
            response.data = Arrays.asList(result);
            return response;
            }
        catch (Exception ex) {
            throw new IOException(ex);
        }
    }
    
}
