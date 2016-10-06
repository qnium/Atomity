/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.qnium.common.backend.core;

import com.qnium.common.backend.assets.dataobjects.CountResponseMessage;
import com.qnium.common.backend.assets.dataobjects.RequestMessage;
import com.qnium.common.backend.assets.dataobjects.UpdateRequestParameters;
import com.qnium.common.backend.assets.interfaces.IHandler;
import java.util.HashMap;
import net.jodah.typetools.TypeResolver;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;


public class HandlersManager {
    private static HandlersManager _instance = null;
    
    HashMap<String, HandlerWrapper> _hashHandlers;
    
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
