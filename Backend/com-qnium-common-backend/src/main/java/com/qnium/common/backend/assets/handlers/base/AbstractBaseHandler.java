/*
 * This file is copyrighted by QNIUM LLC
 * And licensed on eclusive/non-exclusive basis under the terms of the license given to the final user 
 * or in terms of the contract concluded between sources licensee and QNIUM LLC
 */
package com.qnium.common.backend.assets.handlers.base;

import com.fasterxml.jackson.core.type.TypeReference;
import com.qnium.common.backend.assets.dataobjects.CollectionResponseMessage;
import com.qnium.common.backend.assets.dataobjects.CountResponseMessage;
import com.qnium.common.backend.assets.dataobjects.CreateRequestParameters;
import com.qnium.common.backend.assets.dataobjects.ReadRequestParameters;
import com.qnium.common.backend.assets.dataobjects.RequestMessage;
import com.qnium.common.backend.assets.interfaces.IAfterRequestHandler;
import com.qnium.common.backend.assets.interfaces.IBeforeRequestHandler;
import com.qnium.common.backend.assets.interfaces.IHandler;
import com.qnium.common.backend.core.HandlerWrapper;
import com.qnium.common.backend.exceptions.CommonException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kirill Zhukov
 */
public abstract class AbstractBaseHandler<I,O> implements IHandler<I, O> {
    public HandlerWrapper getWrapper()
    {
       return new HandlerWrapper(
                new TypeReference<I>() {},
                new TypeReference<O>() {},
                this);
    }
    
    List<IBeforeRequestHandler<I>> _beforeHandlers;
    List<IAfterRequestHandler<I, O>> _afterHandlers;
    
    public AbstractBaseHandler() {
        _beforeHandlers = new ArrayList<>();
        _afterHandlers = new ArrayList<>();
    }

    public void addBeforeHandler(IBeforeRequestHandler handler)
    {
        _beforeHandlers.add(handler);
    }
    
    public void addAfterHandler(IAfterRequestHandler handler)
    {
        _afterHandlers.add(handler);
    } 
    
    
    @Override
    public O process(I request) throws IOException, CommonException{
        for ( IBeforeRequestHandler<I> handler : _beforeHandlers )
        {
            request = handler.process(request);
        }
        
        O response = processRequest(request);
        
        for ( IAfterRequestHandler<I, O> handler : _afterHandlers )
        {
            response = handler.process(request, response);
        }
        
        return response;
    }
    
    abstract public O processRequest(I request) throws IOException, CommonException;
    
}
