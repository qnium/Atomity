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
import com.qnium.common.paymentsystem.core.messages.OrderPaymentRequest;
import com.qnium.common.paymentsystem.dao.OrderManager;
import com.qnium.common.paymentsystem.dao.PaymentMethodManager;
import com.qnium.common.paymentsystem.dataobjects.Order;
import com.qnium.common.paymentsystem.dataobjects.PaymentPage;
import java.io.IOException;

/**
 *
 * @author Administrator
 */
public class OrdersStartPayment implements IHandler {

    @Override
    public Object process(Object request) throws IOException {
        try {
            RequestMessage<OrderPaymentRequest> req = (RequestMessage<OrderPaymentRequest>)request;

            long customerId = PaymentSystem.getProvider().getCurrentCustomerId(req.data.sessionKey);
            Order order = OrderManager.getInstance().getOrderById(customerId, req.data.orderId);
            
            PaymentPage paymentPage = PaymentMethodManager.getInstance()
                    .requestPayment(req.data.paymentMethodId, order);
            ObjectResponseMessage<PaymentPage> response = new ObjectResponseMessage<>();
            response.result = paymentPage;
            return response;
        }
        catch (Exception ex) {
            throw new IOException(ex);
        }
    }
}
