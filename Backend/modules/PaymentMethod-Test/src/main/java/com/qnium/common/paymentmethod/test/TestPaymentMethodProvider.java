/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.paymentmethod.test;

import com.qnium.common.paymentsystem.dao.OrderManager;
import com.qnium.common.paymentsystem.dataobjects.PaymentPage;
import com.qnium.common.paymentsystem.dataobjects.PaymentParameters;
import com.qnium.common.paymentsystem.interfaces.PaymentMethodProvider;
import java.util.Map;


public class TestPaymentMethodProvider implements PaymentMethodProvider {
    private final String PROVIDER_NAME = "Test";
    
    @Override
    public String getName() {
        return PROVIDER_NAME;
    }

    @Override
    public PaymentPage requestPayment(PaymentParameters params) throws Exception {
        OrderManager.getInstance().confirmPaymentForOrder(params.getOrderId());
        
        PaymentPage paymentPage = new PaymentPage();
        paymentPage.setUrl("http://example.org/testpaymentmethod");
        return paymentPage;
    }

    @Override
    public void init(Map<String, String> parameters) {
        
    }
    
}
