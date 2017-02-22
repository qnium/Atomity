/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.paymentsystem.interfaces;

import com.qnium.common.backend.core.FieldFilter;
import com.qnium.common.backend.core.FilteredResult;
import com.qnium.common.paymentsystem.dataobjects.Customer;
import com.qnium.common.paymentsystem.dataobjects.Feature;
import java.util.List;

/**
 *
 * @author Drozhin
 */
public interface IPaymentSystemProvider
{
    List<Customer> getCustomers(List<Long> customerIds) throws Exception;
    
    List<Customer> getCustomersExcludedFromIdList(List<Long> customerIds) throws Exception;
    
    FilteredResult<Customer> customersRead(List<FieldFilter> filters, long startIndex, long count) throws Exception;
    
    long getCurrentCustomerId(String sessionKey) throws Exception;
    
    List<Feature> getFeatures(List<Long> featureIds) throws Exception;
    
    List<Feature> getAllFeatures() throws Exception;
}
