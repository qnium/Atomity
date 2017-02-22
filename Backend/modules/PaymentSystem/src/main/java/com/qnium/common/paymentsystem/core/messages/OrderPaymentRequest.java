package com.qnium.common.paymentsystem.core.messages;

import com.qnium.common.backend.assets.dataobjects.CommonRequestParameters;

public class OrderPaymentRequest extends CommonRequestParameters {
    public String paymentMethodId;
    public long orderId;
}
