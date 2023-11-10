/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qnium.common.client;

/**
 *
 * @author Acer
 */
public class DataProvider extends GenericDataProvider<Object> {
    
    public DataProvider(String sessionKey, String apiEndpoint, Integer timeout)
    {
        super(Object.class);
        this.sessionKey = sessionKey;
        this._apiEndpoint = apiEndpoint;
        this.timeout = timeout >= 0 ? timeout : DEFAULT_TIMEOUT;
    }
    
}
