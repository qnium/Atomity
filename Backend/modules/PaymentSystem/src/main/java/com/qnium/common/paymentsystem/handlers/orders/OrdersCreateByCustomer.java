/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.paymentsystem.handlers.orders;

import com.qnium.common.backend.assets.dataobjects.CreateRequestParameters;
import com.qnium.common.backend.assets.dataobjects.ObjectResponseMessage;
import com.qnium.common.backend.assets.dataobjects.RequestMessage;
import com.qnium.common.backend.assets.interfaces.IHandler;
import com.qnium.common.paymentsystem.core.PaymentSystem;
import com.qnium.common.paymentsystem.dao.OrderManager;
import com.qnium.common.paymentsystem.dataobjects.Order;
import java.io.IOException;

/**
 *
 * @author Administrator
 */
public class OrdersCreateByCustomer implements IHandler {

    @Override
    public Object process(Object request) throws IOException {
        try {
            RequestMessage<CreateRequestParameters<Order>> req = 
                    (RequestMessage<CreateRequestParameters<Order>>)request;

            Order order = req.data.entity;
            order.amount = OrderManager.getInstance().calcOrderAmount(order, false);
            
            long customerId = PaymentSystem.getProvider().getCurrentCustomerId(req.data.sessionKey);
            order.customerId = customerId;
            
            long orderId = OrderManager.getInstance().createAndReturnId(order);
            
            ObjectResponseMessage<Long> response = new ObjectResponseMessage<>();
            response.result = orderId;
            return response;
        }
        catch (Exception ex) {
            throw new IOException(ex);
        }
    }
}
