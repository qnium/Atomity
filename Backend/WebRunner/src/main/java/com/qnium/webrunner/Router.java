/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.webrunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qnium.webrunner.helpers.ParamsConverter;
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
    
    public class ROUTE{
        public static final String RT_ERROR = "RT_ERROR";
        public static final String RT_NOT_FOUND = "RT_NOT_FOUND";
        public static final String RT_DEFAULT = "RT_DEFAULT";
    }
    
    ConcurrentHashMap<String, RouteHandlerWrapper> _routes = new ConcurrentHashMap<>();
    
    private String _staticPath;
    private String _basePath;

    public String getStaticPath() {
        return _staticPath;
    }
        
    public void setStaticPath(String path)
    {
        this._staticPath = path;
    }
    
    public String getBasePath() {
        return _basePath;
    }
        
    public void setBasePath(String path)
    {
        this._basePath = path;
    }
    
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
    
//    public <T> void route(ROUTE route, IRouteHandler<T> handler, Class<T> dataClass) throws Exception
//    {
//        synchronized(_routes)
//        {
//            _routes.put(route.toString(), new RouteHandlerWrapper(handler, dataClass));
//        }
//    }
    
    public <T> void route(String route, IRouteHandler<T> handler, Class<T> dataClass) throws Exception
    {
        synchronized(_routes)
        {
            _routes.put(route, new RouteHandlerWrapper(handler, dataClass));
        }
    }
    
    protected void processRoute(String route, String query, Map<String, String[]> params, PrintWriter out)
    {
       synchronized(_routes)
        {
           RouteHandlerWrapper handlerWrapper = _routes.get(route);
           if (handlerWrapper == null)
           {
               handlerWrapper = _routes.get(ROUTE.RT_DEFAULT.toString());
           }
           
           if (handlerWrapper != null)
           {
               try {
                   Object paramsObject = handlerWrapper._dataClass != null ? ParamsConverter.convert(params, handlerWrapper._dataClass) : null;
                   handlerWrapper._handler.handle(new Request(route, query), paramsObject , out);
               } catch (Exception ex) {
                   Logger.getLogger(Router.class.getName()).log(Level.SEVERE, null, ex.getMessage());
                   ex.printStackTrace();
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
