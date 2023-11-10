/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.qnium.common.backend.assets.dataobjects.CollectionResponseMessage;
import com.qnium.common.backend.assets.dataobjects.CountResponseMessage;
import com.qnium.common.backend.assets.dataobjects.CreateRequestParameters;
import com.qnium.common.backend.assets.dataobjects.ReadRequestParameters;
import com.qnium.common.backend.assets.dataobjects.RequestMessage;
import com.qnium.common.backend.core.FieldFilter;
import com.qnium.common.client.exceptions.DataProviderException;
import com.qnium.common.definitions.ErrorCode;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author user
 */
public class GenericDataProvider<T> {
    
    private static final HashMap<Class, GenericDataProvider> instances = new HashMap<>();
    private Class<T> _t;
    private JavaType returnReadType;
    private String _entity;
    
    public static synchronized <T> GenericDataProvider<T> getInstance(Class<T> t)
    {
        if(!instances.containsKey(t)) {
            instances.put(t, new GenericDataProvider(t));
        }
        
        return instances.get(t);
    }
    
    static Integer DEFAULT_TIMEOUT = 20000;
    static String TIMEOUT_ERROR_MESSAGE = "Request timed out. Please check your Internet connection.";

    static String sessionKey;
    String _apiEndpoint;
    Integer timeout;

    public GenericDataProvider(Class<T> t) {
        _t = t;
        this.returnReadType = TypeFactory.defaultInstance().constructParametricType(CollectionResponseMessage.class, _t);
        this.sessionKey = sessionKey;
        this.timeout = (timeout != null && timeout >= 0) ? timeout : DEFAULT_TIMEOUT;
    }
    
    public CollectionResponseMessage<T> read(String entity, String action, long startIndex, long count, List<FieldFilter> filter) throws DataProviderException
    {
        ReadRequestParameters req = new ReadRequestParameters();
        req.startIndex = startIndex;
        req.count = count;
        req.filter = filter;
        
        return this.executeAction(entity, action, req, returnReadType);
    }
    
    public CountResponseMessage create(String entity, String action, T data) throws DataProviderException
    {
        CreateRequestParameters req = new CreateRequestParameters<T>();
        req.entity = data;
        
        return this.executeAction(entity, action, req, new TypeReference<CountResponseMessage>(){});
    }
    
    private final String USER_AGENT = "Mozilla/5.0";
//    public <O> O executeAction(String entity, String action, Object data, TypeReference<O> responseType) throws UnsupportedEncodingException, IOException, Exception {

    
    public <O> O executeAction(String entity, String action, Object data, TypeReference<O> responseType) throws DataProviderException {
        return executeAction(entity, action, data, TypeFactory.defaultInstance().constructType(responseType));
    }
    
    public <O> O executeAction(String entity, String action, Object data, JavaType responseType) throws DataProviderException {
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

        String url = _apiEndpoint;

        URL obj;
        HttpURLConnection con;

        try {
            obj = new URL(url);
        } catch (MalformedURLException ex) {
            throw new DataProviderException(ErrorCode.API_URL_ERROR, ex.getMessage(), ex);
        }

        try {
            con = (HttpURLConnection) obj.openConnection();
        } catch (IOException ex) {
            throw new DataProviderException(ErrorCode.CONNECTION_ERROR, ex.getMessage(), ex);
        }

        try {
            con.setRequestMethod("POST");
        } catch (ProtocolException ex) {
        }

        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setDoOutput(true);

        try {
            mapper.writeValue(con.getOutputStream(), request);
        } catch (IOException ex) {
            throw new DataProviderException(ErrorCode.SEND_REQUEST_ERROR, ex.getMessage(), ex);
        }

        int responseCode;

        try {
            responseCode = con.getResponseCode();
        } catch (IOException ex) {
            throw new DataProviderException(ErrorCode.GET_RESPONSE_CODE_ERROR, ex.getMessage(), ex);
        }

        if (responseCode != 200) {
            throw new DataProviderException(ErrorCode.INCORRECT_RESPONSE_CODE, "ResponseCode: " + responseCode);
        }

        String result;
        try {
            //TypeReference responseMessageType = new TypeReference<O>() {};
            result = CharStreams.toString(new InputStreamReader(con.getInputStream(), Charsets.UTF_8));
        } catch (IOException ex) {
            throw new DataProviderException(ErrorCode.GET_RESPONSE_ERROR, ex.getMessage(), ex);
        }

        try {
            return (O) mapper.readValue(result, responseType);
        } catch (IOException ex) {
            throw new DataProviderException(ErrorCode.PROCESS_RESPONSE_ERROR, ex.getMessage(), ex);
        }
    }

    public void Init(String apiEndpoint) {
        _apiEndpoint = apiEndpoint;
    }

    public static void setSessionKey(String key) {
        sessionKey = key;
    }
}
