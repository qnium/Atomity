/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.paymentsystem.handlers.subscriptions;

import com.qnium.common.backend.assets.dataobjects.CollectionResponseMessage;
import com.qnium.common.backend.assets.dataobjects.ReadRequestParameters;
import com.qnium.common.backend.assets.dataobjects.RequestMessage;
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
public class GetFeaturesHandler implements IHandler{

    @Override
    public Object process(Object request) throws IOException {
        try{
            RequestMessage<ReadRequestParameters> req = (RequestMessage<ReadRequestParameters>)request;
            
            AuthManager.getInstance().verifyAdminPrivileges(req.data.sessionKey);
                      
            SubscriptionManager sm = SubscriptionManager.getInstance();
            Feature[] result = sm.getFeatures();
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