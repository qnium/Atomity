/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.backend.assets.handlers.generic;

import com.qnium.common.backend.core.EntityManagerStorage;
import java.io.IOException;
import com.qnium.common.backend.assets.interfaces.IEntityManager;
import com.qnium.common.backend.assets.interfaces.IHandler;
import com.qnium.common.backend.assets.dataobjects.CountResponseMessage;
import com.qnium.common.backend.assets.dataobjects.DeleteRequestParameters;
import com.qnium.common.backend.assets.dataobjects.RequestMessage;
import com.qnium.common.backend.exceptions.CommonException;

/**
 *
 * @author
 */
public class DeleteHandler implements IHandler {

    public DeleteHandler() throws Exception {
    }
    
    @Override
    public Object process(Object request) throws IOException, CommonException
    {
       try
       {
           RequestMessage<DeleteRequestParameters> req = (RequestMessage<DeleteRequestParameters>)request;
           
           IEntityManager em = EntityManagerStorage.getInstance().getEntityManager(req.entityName);
           
           em.delete(req.data.entities);
           
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
