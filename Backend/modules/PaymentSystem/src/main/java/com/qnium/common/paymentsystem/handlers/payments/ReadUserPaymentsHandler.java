/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.paymentsystem.handlers.payments;

import java.io.IOException;
import com.qnium.common.backend.assets.dataobjects.CollectionResponseMessage;
import com.qnium.common.backend.assets.dataobjects.ReadRequestParameters;
import com.qnium.common.backend.assets.dataobjects.RequestMessage;
import com.qnium.common.backend.assets.definitions.FieldOperations;
import com.qnium.common.backend.assets.interfaces.IHandler;
import com.qnium.common.backend.core.AuthManager;
import com.qnium.common.backend.core.FieldFilter;
import com.qnium.common.backend.core.FilteredResult;
import com.qnium.common.paymentsystem.core.PaymentSystem;
import com.qnium.common.paymentsystem.dao.PaymentManager;
import com.qnium.common.paymentsystem.dataobjects.Payment;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Drozhin
 */
public class ReadUserPaymentsHandler implements IHandler {

    @Override
    public Object process(Object request) throws IOException {
        try
        {
            RequestMessage<ReadRequestParameters> req = (RequestMessage<ReadRequestParameters>)request;
            
            //List<FieldFilter> filters = req.data.filter;
            
            boolean isAdminSesseion = false;
            try {
                AuthManager.getInstance().verifyAdminPrivileges(req.data.sessionKey);
                isAdminSesseion = true;
            } catch (Exception ex) {};

            if(!isAdminSesseion)
            {
                long customerId = PaymentSystem.getProvider().getCurrentCustomerId(req.data.sessionKey);
                
                if(req.data.filter == null){
                    req.data.filter = new ArrayList<>();
                }
                
                FieldFilter additionFilter = new FieldFilter();
                
                additionFilter.field = Payment.CUSTOMER_ID;
                additionFilter.operation = FieldOperations.EQ;
                additionFilter.value = customerId;
                
                req.data.filter.add(additionFilter);
            }

            CollectionResponseMessage response = new CollectionResponseMessage();
            PaymentManager paymentManager;
            try {
                paymentManager = PaymentManager.getInstance();
                FilteredResult result = paymentManager.read(req.data.filter, req.data.startIndex, req.data.count);
                response.totalCounter = result.totalCounter;
                response.data = result.data;
            } catch (Exception ex) {
                throw new IOException(ex);
            }

            return response;
        }
        catch (Exception ex) {
            throw new IOException(ex);
        }
    }
}
