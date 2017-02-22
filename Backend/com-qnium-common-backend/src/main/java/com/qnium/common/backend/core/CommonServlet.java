/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.qnium.common.backend.core;

import com.qnium.common.backend.assets.dataobjects.RequestMessage;
import com.qnium.common.backend.assets.dataobjects.ResponseMessage;
import com.qnium.common.backend.exceptions.CommonException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

/**
 *
 * @author
 */
public class CommonServlet extends HttpServlet
{    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("<h1>API servlet uses post method</h1>");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    {
        String action = "";
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationConfig.Feature.AUTO_DETECT_GETTERS, false);
        mapper.configure(SerializationConfig.Feature.AUTO_DETECT_IS_GETTERS, false);
        BufferedReader requestReader = null;
        try
        {
            requestReader = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8));
            String json = requestReader.readLine();
            RequestMessage mainMessage = mapper.readValue(json, RequestMessage.class);
            action = mainMessage.entityName + "." + mainMessage.action;
            response.setContentType("application/json");            
            
            AuthManager.getInstance().checkPermission(mainMessage.sessionKey, mainMessage.entityName, mainMessage.action);
            
            HandlerWrapper wrapper = HandlersManager.getInstance().getHandler(action);
            if (wrapper == null){
                throw (new Exception("no handler for " + action));
            }
            Object requestMessage = mapper.readValue(json, wrapper.getRequest());
            Object responseMessage = wrapper.getHandler().process(requestMessage);
            
            mapper.writeValue(response.getOutputStream(), responseMessage);            
        }
        catch (Exception ex)
        {
            Logger.log.error("System error at action: \"" + action + "\".", ex);
            try
            {
                ResponseMessage errorResponse = new ResponseMessage();
                
                if(CommonException.class.isInstance(ex)){
                    errorResponse.error = ex.getMessage();
                    errorResponse.errorCode = ((CommonException)ex).getErrorCode();
                } else {                
                    errorResponse.error = "Low level error.";
                }
                
                mapper.writeValue(response.getOutputStream(), errorResponse);
            }
            catch (IOException ex1) {
                Logger.log.error("System error at action: \"" + action + "\".", ex1);
            }
        }
        finally
        {
            try {
                requestReader.close();
            }
            catch (IOException ex) {
                Logger.log.error("", ex);
            }
        }
    }   
}
