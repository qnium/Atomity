/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.validation.basic;

import com.qnium.common.validation.IValidator;
import com.qnium.common.validation.ValidationManager;
import com.qnium.common.validation.data.ValidatorItem;
import com.qnium.common.validation.exceptions.ValidationException;
import java.util.List;
import java.util.Optional;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 *
 * @author nbv
 */
public class ValidateScript implements IValidator{
    
    String _validationCode;
    String _error;

    @Override
    public void init(String params, String errorMessage) {
        _validationCode = params;
        _error = errorMessage;
    }

    @Override
    public void validate(String object) throws ValidationException {
        
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        
        try{
            StringBuilder valCode = new StringBuilder("var validate = function(object) { return (");
            valCode.append(_validationCode);
            valCode.append(");};");

            engine.eval(valCode.toString());
            Invocable invocable = (Invocable) engine;
            boolean result = (boolean) invocable.invokeFunction("validate", object);
            if (!result)
                throw new ValidationException(_error);
        } catch(ValidationException ex)
        {
            throw ex;
        }
        catch(Exception ex)
        {
            throw new ValidationException("Error executing JS validator");
        }
        
    }

    @Override
    public String getJSCode() {
        return _validationCode;
    }

    @Override
    public String getError() {
        return _error;
    }
    
}
