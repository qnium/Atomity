/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.paymentsystem.dataobjects;

import com.qnium.common.backend.assets.dataobjects.CommonRequestParameters;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author Drozhin
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalcPaymentParamsRequestParameters extends CommonRequestParameters
{
    public Payment payment;
    
    @JsonCreator
    public CalcPaymentParamsRequestParameters(@JsonProperty("data") Payment data) {
        this.payment = data;
    }    
}
