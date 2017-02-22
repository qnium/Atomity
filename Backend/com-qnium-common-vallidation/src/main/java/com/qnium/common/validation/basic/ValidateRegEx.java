/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.validation.basic;

import com.qnium.common.validation.IValidator;
import com.qnium.common.validation.exceptions.ValidationException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jetty.util.log.Log;

/**
 *
 * @author nbv
 */
public class ValidateRegEx implements IValidator{

    String error;
    String regEx;
    Pattern pattern;
    
    @Override
    public void init(String params, String errorMessage) {
        try {
            regEx = params;
            pattern = Pattern.compile(regEx);
            error = String.format(errorMessage, params);
        } catch(Exception ex)
        {
            //Log.getLogger("").
        }
    }

    @Override
    public void validate(String object) throws ValidationException {
        Matcher matcher = pattern.matcher(object);
	if (!matcher.matches())
            throw new ValidationException(error);
    }

    @Override
    public String getJSCode() {
        return String.format("object.match(/%s/)", regEx);
    }

    @Override
    public String getError() {
        return error;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
