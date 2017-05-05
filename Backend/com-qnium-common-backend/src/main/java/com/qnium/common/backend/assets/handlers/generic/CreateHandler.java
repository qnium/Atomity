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
import com.qnium.common.backend.assets.handlers.base.BaseCreateHandler;
import com.qnium.common.backend.exceptions.CommonException;

/**
 *
 * @author Drozhin
 */
public class CreateHandler extends BaseCreateHandler {

   
    @Override
    public CountResponseMessage processRequest(RequestMessage request) throws IOException, CommonException {
       try
       {
           //RequestMessage<CreateRequestParameters> req = (RequestMessage<CreateRequestParameters>)request;
           
           IEntityManager em = EntityManagerStorage.getInstance().getEntityManager(request.entityName);
           
           em.create(request.data);
           
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
