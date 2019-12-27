/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.qnium.common.backend.core;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.qnium.common.backend.assets.dataobjects.CommonHandlerRequest;
import com.qnium.common.backend.assets.dataobjects.CommonHandlerResponse;
import com.qnium.common.backend.assets.dataobjects.RequestMessage;
import com.qnium.common.backend.assets.dataobjects.ResponseMessage;
import com.qnium.common.backend.assets.interfaces.ICommonRequestHandler;
import com.qnium.common.backend.assets.interfaces.ICommonResponseHandler;
import com.qnium.common.backend.exceptions.CommonException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
        mapper.disable(MapperFeature.AUTO_DETECT_GETTERS);
        mapper.disable(MapperFeature.AUTO_DETECT_IS_GETTERS);
        mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        mapper.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        mapper.registerModule(new JavaTimeModule());
        BufferedReader requestReader = null;
        try
        {
            requestReader = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8));
            String json = requestReader.readLine();
            RequestMessage mainMessage = mapper.readValue(json, RequestMessage.class);
            action = mainMessage.entityName + "." + mainMessage.action;
            
            HandlerWrapper wrapper = HandlersManager.getInstance().getHandler(action);
            if (wrapper == null){
                throw (new Exception("no handler for " + action));
            }
            
            Object requestMessage = mapper.readValue(json, wrapper.getRequest());

            CommonHandlerRequest commonHandlerRequest = new CommonHandlerRequest();
            commonHandlerRequest.request = (RequestMessage)requestMessage;
            List<ICommonRequestHandler> commonRequestHandlers = HandlersManager.getInstance().getCommonRequestHandlers();
            for (ICommonRequestHandler handler : commonRequestHandlers) {
                commonHandlerRequest = handler.process(commonHandlerRequest);
            }            
            
            AuthManager.getInstance().checkPermission(mainMessage.sessionKey, mainMessage.entityName, mainMessage.action);

            Object responseMessage = wrapper.getHandler().process(commonHandlerRequest.request);
            
            CommonHandlerResponse commonHandlerResponse = new CommonHandlerResponse();
            commonHandlerResponse.response = responseMessage;
            List<ICommonResponseHandler> commonResponseHandlers = HandlersManager.getInstance().getCommonResponseHandlers();
            for (ICommonResponseHandler handler : commonResponseHandlers) {
                commonHandlerResponse = handler.process(commonHandlerRequest, commonHandlerResponse);
            }            
            
            response.setContentType("application/json");            
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
