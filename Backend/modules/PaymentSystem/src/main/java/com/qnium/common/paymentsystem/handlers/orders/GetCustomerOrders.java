/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.paymentsystem.handlers.orders;

import com.qnium.common.backend.assets.dataobjects.CollectionResponseMessage;
import com.qnium.common.backend.assets.dataobjects.ReadRequestParameters;
import com.qnium.common.backend.assets.dataobjects.RequestMessage;
import com.qnium.common.backend.assets.interfaces.IHandler;
import com.qnium.common.backend.core.FilteredResult;
import com.qnium.common.paymentsystem.core.PaymentSystem;
import com.qnium.common.paymentsystem.dao.OrderManager;
import java.io.IOException;

/**
 *
 * @author Drozhin
 */
public class GetCustomerOrders implements IHandler
{
    @Override
    public Object process(Object request) throws IOException {
        try
        {
            RequestMessage<ReadRequestParameters> req = (RequestMessage<ReadRequestParameters>)request;
            
            long customerId = PaymentSystem.getProvider().getCurrentCustomerId(req.data.sessionKey);
            FilteredResult orders = OrderManager.getInstance().getCustomerOrdersInfo(customerId, req.data.filter, req.data.startIndex, req.data.count);

            CollectionResponseMessage response = new CollectionResponseMessage();
            response.totalCounter = orders.totalCounter;
            response.data = orders.data;

            return response;
        }
        catch (Exception ex) {
            throw new IOException(ex);
        }
    }    
}
