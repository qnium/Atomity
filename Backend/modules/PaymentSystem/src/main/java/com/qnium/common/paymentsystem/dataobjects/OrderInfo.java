/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.paymentsystem.dataobjects;

import java.util.Date;
import java.util.List;

/**
 *
 * @author Drozhin
 */
public class OrderInfo
{
    public Customer customer;
    public long orderNumber;
    public Date creationDate;
    public double amount;
    public boolean paid;
    public List<OrderInfoItem> items;
}
