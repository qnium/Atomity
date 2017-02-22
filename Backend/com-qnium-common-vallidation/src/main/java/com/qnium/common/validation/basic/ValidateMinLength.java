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
public class ValidateMinLength implements IValidator{

    String error;
    int min = Integer.MIN_VALUE;
    
    @Override
    public void init(String params, String errorMessage) {
        try {
            min = Integer.parseInt(params);
            error = String.format(errorMessage, min);
        } catch(Exception ex)
        {
            //Log.getLogger("").
        }
    }

    @Override
    public void validate(String object) throws ValidationException {
        if ( object.length() < min )
            throw new ValidationException(error);
    }

    @Override
    public String getJSCode() {
        return String.format("object.toString().length >= %d", min);
    }

    @Override
    public String getError() {
        return error;
    }
    
}
