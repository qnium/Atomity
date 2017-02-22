/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.backend.assets.handlers.generic;

import com.qnium.common.backend.assets.dataobjects.CountResponseMessage;
import com.qnium.common.backend.assets.dataobjects.RequestMessage;
import com.qnium.common.backend.assets.interfaces.IContextValueProvider;
import com.qnium.common.backend.assets.interfaces.IEntityManager;
import com.qnium.common.backend.assets.interfaces.IHandler;
import com.qnium.common.backend.core.EntityManagerStorage;
import com.qnium.common.backend.exceptions.CommonException;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author Ivan
 */
public class ContextDeleteHandler implements 
        IHandler<RequestMessage, CountResponseMessage>{

    private final IContextValueProvider provider;
    private final String contextField;
    
    public ContextDeleteHandler(IContextValueProvider provider, String contextField) {
        this.provider = provider;
        this.contextField = contextField;
    }
    
    @Override
    public CountResponseMessage process(RequestMessage req) 
            throws IOException, CommonException {
        try {
            Object entity = req.data;
            
            long contextValue = provider.getContextValue(req.sessionKey);
            
            IEntityManager em = EntityManagerStorage.getInstance()
                .getEntityManager(req.entityName);
            em.selfDelete(Arrays.asList(entity), contextField, contextValue, null, 0);
            
            return new CountResponseMessage(em.getCount());
        }
        catch (CommonException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new IOException(ex);
        }
    }
    
}
