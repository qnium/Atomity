package com.qnium.common.paymentsystem.dataobjects;

/**
 * Set of data required to initialize payment process for customer.
 * @author Ivan Kashtanov
 */
public class PaymentParameters {
    private long orderId;
    private double amount;
    private String description;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
