/*
 * This file is copyrighted by QNIUM LLC
 * And licensed on eclusive/non-exclusive basis under the terms of the license given to the final user 
 * or in terms of the contract concluded between sources licensee and QNIUM LLC
 */
package com.qnium.common.backend.core;

import com.qnium.common.backend.assets.interfaces.IValidationProvider;
import com.qnium.common.backend.exceptions.CommonException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nbv
 */
public class CommonValidationManager {
    
    IValidationProvider _provider;
    
    private CommonValidationManager() {
        _provider = p -> {}; //Default provider confirms validation of anything
    }
    
    public static CommonValidationManager getInstance() {
        return ValidationManagerHolder.INSTANCE;
    }
    
    private static class ValidationManagerHolder {

        private static final CommonValidationManager INSTANCE = new CommonValidationManager();
    }
    
    public void setValidationProvider(IValidationProvider provider)
    {
        _provider = provider;
    }
    
    void validatObject(Object object) throws CommonException
    {
        try {
            _provider.validateObject(object);
        } catch (Exception ex) {
            throw new CommonException(CommonException.VALIDATION_ERROR, ex.getMessage());
        }
    }
}
