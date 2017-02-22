/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.backend.assets.handlers.generic;

import com.qnium.common.backend.core.EntityManagerStorage;
import com.qnium.common.backend.core.FilteredResult;
import java.io.IOException;
import com.qnium.common.backend.assets.interfaces.IEntityManager;
import com.qnium.common.backend.assets.interfaces.IHandler;
import com.qnium.common.backend.assets.dataobjects.CollectionResponseMessage;
import com.qnium.common.backend.assets.dataobjects.RequestMessage;
import com.qnium.common.backend.assets.dataobjects.ReadRequestParameters;
import com.qnium.common.backend.assets.handlers.base.BaseReadHandler;
import com.qnium.common.backend.assets.interfaces.IAfterRequestHandler;
import com.qnium.common.backend.assets.interfaces.IBeforeRequestHandler;
import com.qnium.common.backend.exceptions.CommonException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Drozhin
 */
public class ReadHandler extends BaseReadHandler {
    
    @Override
    public CollectionResponseMessage processRequest(RequestMessage<ReadRequestParameters> request) throws IOException, CommonException {
        try
        {
            
            RequestMessage<ReadRequestParameters> req = (RequestMessage<ReadRequestParameters>)request;
                        
            IEntityManager em = EntityManagerStorage.getInstance().getEntityManager(req.entityName);
            
            FilteredResult result = em.read(req.data.filter, req.data.startIndex, req.data.count);
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
