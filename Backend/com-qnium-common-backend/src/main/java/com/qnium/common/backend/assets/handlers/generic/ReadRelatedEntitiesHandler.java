/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.backend.assets.handlers.generic;

import com.qnium.common.backend.assets.dataobjects.CollectionResponseMessage;
import com.qnium.common.backend.assets.dataobjects.ObjectResponseMessage;
import com.qnium.common.backend.assets.dataobjects.RequestMessage;
import com.qnium.common.backend.assets.interfaces.IEntityManager;
import com.qnium.common.backend.assets.interfaces.IHandler;
import com.qnium.common.backend.core.EntityManagerStorage;
import com.qnium.common.backend.core.FieldFilter;
import com.qnium.common.backend.core.FilteredResult;
import com.qnium.common.backend.exceptions.CommonException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Drozhin
 */
public class ReadRelatedEntitiesHandler implements IHandler<RequestMessage<FieldFilter>,
        ObjectResponseMessage<List<Long>>>
{
    @Override
    public ObjectResponseMessage<List<Long>> process(RequestMessage<FieldFilter> request) throws IOException, CommonException
    {
        ObjectResponseMessage<List<Long>> response = new ObjectResponseMessage();
        
        try
        {            
            if(request.data.value != null && !request.data.value.toString().isEmpty())
            {
                IEntityManager em = EntityManagerStorage.getInstance()
                        .getEntityManager(request.entityName);
                FilteredResult result = em.read(Arrays.asList(request.data), 0, 0);

                List<Long> ids = new ArrayList();

                result.data.stream().forEach(r -> {
                    try {
                        ids.add((Long)em.getDao().extractId(r));
                    } catch (SQLException ex) {
                        //throw ex;
                    }
                });
                response.result = ids;
            }
            
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
