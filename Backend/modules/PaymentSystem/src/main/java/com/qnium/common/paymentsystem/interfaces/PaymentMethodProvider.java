package com.qnium.common.paymentsystem.interfaces;

import com.qnium.common.paymentsystem.dataobjects.PaymentPage;
import com.qnium.common.paymentsystem.dataobjects.PaymentParameters;
import java.util.Map;

/**
 * Interface of a payment method provider. Provides data for 
 * initializing payment process on customer side and notifies payment
 * system about changes in payment status.
 * @author Ivan Kashtanov
 */
public interface PaymentMethodProvider {
    /**
     * Gets unique name of the provider.
     */
    String getName();
    
    /**
     * Gets URL and any other needed data for the page where payment
     * process will be performed.
     * @param params Parameters of the order for which payment is requested.
     * @return Payment page URL.
     * @throws Exception
     */
    PaymentPage requestPayment(PaymentParameters params) throws Exception;
    
    /**
     * Sets parameters of the payment provider needed for processing payment
     * requests.
     * @param parameters 
     */
    void init(Map<String, String> parameters);
}
