/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.paymentsystem.handlers.payments;

import com.qnium.common.backend.assets.dataobjects.ObjectResponseMessage;
import com.qnium.common.backend.assets.dataobjects.RequestMessage;
import com.qnium.common.backend.assets.interfaces.IHandler;
import com.qnium.common.backend.core.AuthManager;
import com.qnium.common.paymentsystem.dao.PaymentManager;
import com.qnium.common.paymentsystem.dataobjects.CalcPaymentParamsRequestParameters;
import java.io.IOException;

/**
 *
 * @author Drozhin
 */
public class CalcPaymentParamsHandler implements IHandler {

    @Override
    public Object process(Object request) throws IOException {        
        try
        {
            RequestMessage<CalcPaymentParamsRequestParameters> req = (RequestMessage<CalcPaymentParamsRequestParameters>)request;
            
            AuthManager.getInstance().verifyAdminPrivileges(req.data.sessionKey);
            ObjectResponseMessage response = new ObjectResponseMessage();
            
            PaymentManager paymentManager = PaymentManager.getInstance();
            response.result = paymentManager.calcPaymentParameters(req.data.payment);
            return response;            
        }
        catch (Exception ex){
            throw new IOException(ex);
        }
        
    }
}
