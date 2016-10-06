/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.paymentsystem.handlers.orders;

import com.qnium.common.backend.assets.dataobjects.ObjectResponseMessage;
import com.qnium.common.backend.assets.dataobjects.RequestMessage;
import com.qnium.common.backend.assets.interfaces.IHandler;
import com.qnium.common.paymentsystem.core.PaymentSystem;
import com.qnium.common.paymentsystem.core.messages.OrderStatusRequest;
import com.qnium.common.paymentsystem.dao.OrderManager;
import java.io.IOException;

/**
 *
 * @author Administrator
 */
public class OrdersGetStatus implements IHandler {

    @Override
    public Object process(Object request) throws IOException {
        try
        {
            RequestMessage<OrderStatusRequest> req = (RequestMessage<OrderStatusRequest>)request;
            long customerId = PaymentSystem.getProvider().getCurrentCustomerId(req.data.sessionKey);
            
            boolean status = OrderManager.getInstance().getOrderById(customerId, req.data.orderId).paid;
            
            ObjectResponseMessage<Boolean> response = new ObjectResponseMessage<>();
            response.result = status;
            return response;
        }
        catch (Exception ex) {
            throw new IOException(ex);
        }
    }
    
}
