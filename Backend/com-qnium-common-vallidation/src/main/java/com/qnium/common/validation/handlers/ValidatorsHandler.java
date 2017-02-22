/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.validation.handlers;

import com.qnium.common.backend.assets.dataobjects.CollectionResponseMessage;
import com.qnium.common.backend.assets.dataobjects.ObjectResponseMessage;
import com.qnium.common.backend.assets.dataobjects.ReadRequestParameters;
import com.qnium.common.backend.assets.dataobjects.RequestMessage;
import com.qnium.common.validation.data.ValidatorItem;
import com.qnium.common.backend.assets.interfaces.IHandler;
import com.qnium.common.backend.core.HandlerWrapper;
import com.qnium.common.backend.exceptions.CommonException;
import com.qnium.common.validation.ValidationManager;
import java.io.IOException;
import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 *
 * @author Kirill Zhukov
 */
public class ValidatorsHandler implements IHandler<RequestMessage, ObjectResponseMessage<List<ValidatorItem>>>{
    
    Class _entity;
    
    public ValidatorsHandler(Class entity)
    {
        _entity = entity;
    }
    
    
    @Override
    public ObjectResponseMessage<List<ValidatorItem>> process(RequestMessage request) throws IOException, CommonException { 
        ObjectResponseMessage<List<ValidatorItem>> response = new ObjectResponseMessage<>();
        response.result = ValidationManager.getInstance().getEntityJSValidators(_entity);
        return response;
    }
    
    public HandlerWrapper getWrapper()
    {
        return new HandlerWrapper(
                new TypeReference<RequestMessage>() {},
                new TypeReference<ObjectResponseMessage<List<ValidatorItem>>>() {},
                this);
    }
    
}
