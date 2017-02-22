package com.qnium.common.paymentsystem.interfaces;

import com.qnium.common.paymentsystem.dataobjects.Order;

public interface IOrderStatusHandler {
    void processOrderCompletion(Order order);
}
