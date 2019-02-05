/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.webrunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qnium.webrunner.interfaces.IRouteHandler;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author admin
 */
public class Router {
    
    ConcurrentHashMap<String, RouteHandlerWrapper> _routes = new ConcurrentHashMap<>();
    
    private Router() {
    }
    
    public static Router getInstance() {
        return RouterHolder.INSTANCE;
    }
    
    private static class RouterHolder {

        private static final Router INSTANCE = new Router();
    }
    
    public <T> void route(String route, IRouteHandler<T> handler) throws Exception
    {
        this.route(route, handler, null);
    }
    
    public <T> void route(String route, IRouteHandler<T> handler, Class<T> dataClass) throws Exception
    {
        synchronized(_routes)
        {
            _routes.put(route, new RouteHandlerWrapper(handler, dataClass));
        }
    }
    
    protected void processRoute(String route, Map<String, String[]> params, PrintWriter out)
    {
       synchronized(_routes)
        {
           RouteHandlerWrapper handlerWrapper = _routes.get(route);
           if (handlerWrapper != null)
           {
               try {
                   
                   final ObjectMapper mapper = new ObjectMapper(); // jackson's objectmapper
                   Object paramsObject = mapper.convertValue(params, handlerWrapper._dataClass);
                   handlerWrapper._handler.handle(new Request(route), paramsObject , out);
                   //Request request = new Request(String.class);
                   //handler.handle();
               } catch (Exception ex) {
                   Logger.getLogger(Router.class.getName()).log(Level.SEVERE, null, ex);
               }
           }
        }
    }

    private class RouteHandlerWrapper<T> {
        
        IRouteHandler _handler;
        Class<T> _dataClass;
        
//        protected Object getParamsInstance() throws Exception
//        {
//            return _dataClass != null ? _dataClass.getDeclaredConstructor().newInstance() : null;
//        }

        public RouteHandlerWrapper(IRouteHandler<T> handler, Class<T> dataClass) throws Exception {
            _handler = handler;
            _dataClass = dataClass;
        }
    }
}
