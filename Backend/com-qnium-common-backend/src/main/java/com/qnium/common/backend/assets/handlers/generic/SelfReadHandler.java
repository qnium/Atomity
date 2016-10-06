/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.backend.assets.handlers.generic;

import com.qnium.common.backend.assets.dataobjects.CollectionResponseMessage;
import com.qnium.common.backend.assets.dataobjects.ReadRequestParameters;
import com.qnium.common.backend.assets.dataobjects.RequestMessage;
import com.qnium.common.backend.assets.interfaces.IEntityManager;
import com.qnium.common.backend.assets.interfaces.IHandler;
import com.qnium.common.backend.core.AuthManager;
import com.qnium.common.backend.core.EntityManagerStorage;
import com.qnium.common.backend.core.FilteredResult;
import com.qnium.common.backend.exceptions.CommonException;
import java.io.IOException;

/**
 *
 * @author Ivan
 */
public class SelfReadHandler 
        implements IHandler<RequestMessage<ReadRequestParameters>, CollectionResponseMessage> 
{
    
    private final String idField;
    private final String typeField;
    
    /**
     * Creates new instance of handler with id-only filtering.
     * @param idField Name of the field that will be filtered based on 
     * account ID that corresponds to session key from request.
     */
    public SelfReadHandler(String idField) throws Exception {
        this(idField, null);
    }
    
    /**
     * Creates new instance of handler with id and type filtering.
     * @param idField Name of the field that will be filtered based on 
     * account ID that corresponds to session key from request.
     * @param typeField Name of the field that will be filtered based on 
     * account type that corresponds to session key from request.
     * @throws Exception if idField is not set
     */
    public SelfReadHandler(String idField, String typeField) throws Exception {
        this.idField = idField;
        if (idField == null || idField.isEmpty()) {
            throw new Exception("ID field must be set for self-read handler");
        }
        
        this.typeField = typeField;
    }

    @Override
    public CollectionResponseMessage process(RequestMessage<ReadRequestParameters> req) 
            throws IOException, CommonException {
        try {
            AuthManager am = AuthManager.getInstance();
            long accountId = am.getAccountId(req.sessionKey);
            int accountType = am.getAccountType(req.sessionKey);
            
            IEntityManager em = EntityManagerStorage.getInstance()
                .getEntityManager(req.entityName);
            FilteredResult result = em.selfRead(req.data.filter, req.data.startIndex, req.data.count,
                    idField, accountId, typeField, accountType);
            
            CollectionResponseMessage response = new CollectionResponseMessage();
            response.totalCounter = result.totalCounter;
            response.data = result.data;
            
            return response;
        }
        catch (CommonException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new IOException(ex);
        }
    }
    
}
