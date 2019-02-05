/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.webrunner;

import com.qnium.webrunner.interfaces.IRouteHandler;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author admin
 */
public class Router {
    
    ConcurrentHashMap<String, IRouteHandler> _routes = new ConcurrentHashMap<>();
    
    private Router() {
    }
    
    public static Router getInstance() {
        return RouterHolder.INSTANCE;
    }
    
    private static class RouterHolder {

        private static final Router INSTANCE = new Router();
    }
    
    public <T> void route(String route, IRouteHandler<T> handler, Class<T> dataClass)
    {
        synchronized(_routes)
        {
            _routes.put(route, handler);
        }
    }
    
    protected void processRoute(String route, Map<String, String[]> params, PrintWriter out)
    {
       synchronized(_routes)
        {
           IRouteHandler handler = _routes.get(route);
           if (handler != null)
           {
               Request request = new Request(String.class);
               //handler.handle();
           }
        }
    }
}
