/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.qnium.common.backend.core;

import com.qnium.common.backend.assets.interfaces.ICommonRequestHandler;
import com.qnium.common.backend.assets.interfaces.ICommonResponseHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class HandlersManager<TCommonHandlerContext> {
    private static HandlersManager _instance = null;
    
    HashMap<String, HandlerWrapper> _hashHandlers;
    List<ICommonRequestHandler> _commonReqeustHandlers;
    List<ICommonResponseHandler> _commonResponseHandlers;
    
    public static synchronized HandlersManager getInstance()
    {
        if ( _instance == null )
        {
            _instance = new HandlersManager();
        }
        
        return _instance;
    }
    
    private HandlersManager()
    {   
        _hashHandlers = new HashMap<>();
        _commonReqeustHandlers = new ArrayList();
        _commonResponseHandlers = new ArrayList();
    }
    
    public void addCommonRequestHandler(ICommonRequestHandler<TCommonHandlerContext> handler) {
        _commonReqeustHandlers.add(handler);
    }
    
    public List<ICommonRequestHandler> getCommonRequestHandlers() {
        return _commonReqeustHandlers;
    }
    
    public void addCommonResponseHandler(ICommonResponseHandler<TCommonHandlerContext> handler) {
        _commonResponseHandlers.add(handler);
    }
    
    public List<ICommonResponseHandler> getCommonResponseHandlers() {
        return _commonResponseHandlers;
    }
    
    public HandlerWrapper getHandler(String action)
    {
        return (HandlerWrapper) _hashHandlers.get(action);
    }
    
    public void addHandler(String handlerName, HandlerWrapper handlerWrapper) throws Exception
    {
        if(!_hashHandlers.containsKey(handlerName)){
            _hashHandlers.put(handlerName, handlerWrapper);
        } else {
            throw new Exception(String.format("Handler \"%s\" already exists in storage", handlerName));
        }
    }
    /*
    public void addHandler(String handlerName, IHandler handler)
    {
        Class<?> classes = TypeResolver.resolveRawArgument(handler.getClass(), IHandler.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.getTypeFactory().
        handlersManager.addHandler(handlerName, new HandlerWrapper(
                new TypeReference<RequestMessage<UpdateRequestParameters<Merchant>>>() {},
                new TypeReference<classes[1]>() {},
                handler));

    }
    */
}
