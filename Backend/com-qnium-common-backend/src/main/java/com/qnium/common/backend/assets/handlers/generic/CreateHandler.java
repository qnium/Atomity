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
import com.qnium.common.backend.assets.dataobjects.CreateRequestParameters;
import com.qnium.common.backend.assets.dataobjects.RequestMessage;
import com.qnium.common.backend.exceptions.CommonException;

/**
 *
 * @author Drozhin
 */
public class CreateHandler implements IHandler {

    public CreateHandler() throws Exception {
    }
    
    @Override
    public Object process(Object request) throws IOException, CommonException 
    {
       try
       {
           RequestMessage<CreateRequestParameters> req = (RequestMessage<CreateRequestParameters>)request;
           
           IEntityManager em = EntityManagerStorage.getInstance().getEntityManager(req.entityName);
           
           em.create(req.data.entity);
           
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
