/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.paymentsystem.dao;

import com.qnium.common.paymentsystem.dataobjects.Order;
import com.qnium.common.paymentsystem.dataobjects.PaymentPage;
import com.qnium.common.paymentsystem.dataobjects.PaymentParameters;
import com.qnium.common.paymentsystem.interfaces.PaymentMethodProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class PaymentMethodManager {
    private static PaymentMethodManager instance;
    
    private final HashMap<String, PaymentMethodProvider> paymentMethodProviders;
    
    public static synchronized PaymentMethodManager getInstance() {
        if (instance == null) {
            instance = new PaymentMethodManager();
        }
        return instance;
    }
    
    private PaymentMethodManager() {
        paymentMethodProviders = new HashMap<>();
    }
    
    public void registerProvider(PaymentMethodProvider provider, Map<String, String> parameters) {
        provider.init(parameters);
        paymentMethodProviders.put(provider.getName(), provider);
    }
    
    private PaymentMethodProvider getProvider(String name) {
        return paymentMethodProviders.get(name);
    }
    
    public List<PaymentMethodProvider> getAvailableProviders() {
        return new ArrayList<>(paymentMethodProviders.values());
    }
    
    public PaymentPage requestPayment(String providerName, Order order) throws Exception {
        PaymentParameters paymentParams = new PaymentParameters();
        paymentParams.setAmount(order.amount);
        paymentParams.setOrderId(order.id);
        paymentParams.setDescription(String.format("Заказ № %s", order.id));
            
        return getProvider(providerName).requestPayment(paymentParams);
    }
}
