/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.backend.assets.handlers.generic;

import com.qnium.common.backend.assets.dataobjects.CollectionResponseMessage;
import com.qnium.common.backend.assets.dataobjects.ReadRequestParameters;
import com.qnium.common.backend.assets.dataobjects.RequestMessage;
import com.qnium.common.backend.assets.definitions.FieldOperations;
import com.qnium.common.backend.assets.interfaces.IEntityManager;
import com.qnium.common.backend.assets.interfaces.IContextValueProvider;
import com.qnium.common.backend.assets.interfaces.IHandler;
import com.qnium.common.backend.core.EntityManagerStorage;
import com.qnium.common.backend.core.FieldFilter;
import com.qnium.common.backend.core.FilteredResult;
import com.qnium.common.backend.exceptions.CommonException;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Ivan
 */
public class ContextReadHandler 
        implements IHandler<RequestMessage<ReadRequestParameters>, CollectionResponseMessage> {
    
    private final IContextValueProvider provider;
    private final String filterField;
    private final String filterOperation;
    
    public ContextReadHandler(IContextValueProvider provider, String filterField) {
        this(provider, filterField, FieldOperations.EQ);
    }

    public ContextReadHandler(IContextValueProvider provider, String filterField, String filterOperation) {
        this.provider = provider;
        this.filterField = filterField;
        this.filterOperation = filterOperation;
    }
    
    @Override
    public CollectionResponseMessage process(RequestMessage<ReadRequestParameters> req) 
            throws IOException, CommonException {
        try {
            ReadRequestParameters data = req.data;
            
            long filterValue = provider.getContextValue(req.sessionKey);
            
            IEntityManager em = EntityManagerStorage.getInstance()
                .getEntityManager(req.entityName);
            FilteredResult result = em.selfRead(
                    data.filter, data.startIndex, data.count,
                    filterField, filterValue, null, 0);
            
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
