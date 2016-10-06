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
public class ValidateRequired implements IValidator{

    String error;
    
    @Override
    public void init(String params, String errorMessage) {
        try {
            if (errorMessage == null || errorMessage.isEmpty())
                errorMessage = "Please enter all required fields";
            error = errorMessage;
        } catch(Exception ex)
        {
            //Log.getLogger("").
        }
    }

    @Override
    public void validate(String object) throws ValidationException {
        if ( object == null || object.isEmpty() )
            throw new ValidationException(error);
    }

    @Override
    public String getJSCode() {
        return "object != undefined && object !== '' && object != null";
    }

    @Override
    public String getError() {
        return error;
    }
    
}
