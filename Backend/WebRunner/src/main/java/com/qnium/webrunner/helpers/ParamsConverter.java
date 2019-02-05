/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.webrunner.helpers;

import java.util.Map;

/**
 *
 * @author admin
 */
public class ParamsConverter {
    public static Object convert(Map<String, String[]> params, Class<?> clazz) throws Exception
    {
        Object result = clazz.getConstructor().newInstance();

        
        
        return result;
    }
}
