/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.qnium.common.backend.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.qnium.common.backend.assets.interfaces.IHandler;

/**
 *
 * @author
 */
public class HandlerWrapper
{
    private final TypeReference _request;
    private final TypeReference _response;
    private final IHandler _handler;

    public TypeReference getRequest() {
        return _request;
    }

    public TypeReference getResponse() {
        return _response;
    }

    public IHandler getHandler() {
        return _handler;
    }
    
    public HandlerWrapper(TypeReference request, TypeReference response, IHandler handler)
    {
        _handler = handler;
        _request = request;
        _response = response;
    }
}
