/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.validation.basic;

import com.qnium.common.validation.IValidator;
import com.qnium.common.validation.exceptions.ValidationException;
import org.eclipse.jetty.util.log.Log;

/**
 *
 * @author nbv
 */
public class ValidateMaxLength implements IValidator{

    String error;
    int max = Integer.MAX_VALUE;
    
    @Override
    public void init(String params, String errorMessage) {
        try {
            max = Integer.parseInt(params);
            error = String.format(errorMessage, max);
        } catch(Exception ex)
        {
            //Log.getLogger("").
        }
    }

    @Override
    public void validate(String object) throws ValidationException {
        if ( object.length() > max )
            throw new ValidationException(error);
    }

    @Override
    public String getJSCode() {
        return String.format("object.toString().length <= %d", max);
    }

    @Override
    public String getError() {
        return error;
    }
    
}
