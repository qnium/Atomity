/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.qnium.common.backend.assets.dataobjects.CollectionResponseMessage;
import com.qnium.common.backend.assets.dataobjects.RequestMessage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import com.qnium.common.backend.core.AuthManager;
import com.qnium.common.backend.core.HandlerWrapper;
import com.qnium.common.backend.core.HandlersManager;
import com.qnium.common.backend.core.Logger;
import com.qnium.common.backend.exceptions.CommonException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.compress.utils.IOUtils;

/**
 *
 * @author user
 */

public class DataProvider {
    static Integer DEFAULT_TIMEOUT = 20000;
    static String TIMEOUT_ERROR_MESSAGE = "Request timed out. Please check your Internet connection.";
    
    String sessionKey;
    String apiEndpoint;
    Integer timeout;
    
    public DataProvider(String sessionKey, String apiEndpoint, Integer timeout)
    {
        this.sessionKey = sessionKey;
        this.apiEndpoint = apiEndpoint;
        this.timeout = timeout <= 0 ? timeout : DEFAULT_TIMEOUT;

    }
    private final String USER_AGENT = "Mozilla/5.0";
    public <O> O executeAction(String entity, String action, Object data, TypeReference<O> responseType) throws UnsupportedEncodingException, IOException, Exception {
            RequestMessage request = new RequestMessage<>(data);
            request.action = action;
            request.entityName = entity;
            request.sessionKey = this.sessionKey;      
            
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(MapperFeature.AUTO_DETECT_GETTERS);
            mapper.disable(MapperFeature.AUTO_DETECT_IS_GETTERS);
            mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
            mapper.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
            mapper.registerModule(new JavaTimeModule());
       
            String url = this.apiEndpoint;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");


            con.setDoOutput(true);            
            mapper.writeValue(con.getOutputStream(), request);
            
            int responseCode = con.getResponseCode();
            if (responseCode!=200)
                throw new Exception("responseCode!=200");
            
            //TypeReference responseMessageType = new TypeReference<O>() {};
            String result = CharStreams.toString(new InputStreamReader(
      con.getInputStream(), Charsets.UTF_8));
            O response = (O) mapper.readValue(result, responseType);
            return response;
            
    }   
    
    void Init(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }

    void setSessionKey(String key) {
        this.sessionKey = key;
    }
}
    
   
