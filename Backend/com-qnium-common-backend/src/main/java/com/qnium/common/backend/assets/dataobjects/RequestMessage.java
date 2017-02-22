/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.backend.assets.dataobjects;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author Drozhin
 * @param <T>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestMessage<T>
{
    public String sessionKey;
    public String entityName;
    public String action;
    public String reportFormat;
    public boolean downloadReport;
    public T data;
    
    @JsonCreator
    public RequestMessage(@JsonProperty("data") T data) {
        this.data = data;
    }    
}
