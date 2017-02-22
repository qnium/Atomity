/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.backend.assets.handlers.generic;

import com.qnium.common.backend.core.AuthManager;
import com.qnium.common.backend.core.EntityManagerStorage;
import com.qnium.common.backend.assets.definitions.SecurityLevel;
import java.io.IOException;
import com.qnium.common.backend.assets.interfaces.IEntityManager;
import com.qnium.common.backend.assets.interfaces.IHandler;
import com.qnium.common.backend.assets.dataobjects.CountResponseMessage;
import com.qnium.common.backend.assets.dataobjects.RequestMessage;
import com.qnium.common.backend.assets.dataobjects.UpdateRequestParameters;
import com.qnium.common.backend.assets.handlers.base.BaseUpdateHandler;
import com.qnium.common.backend.exceptions.CommonException;

/**
 *
 * @author
 */
public class UpdateHandler extends BaseUpdateHandler {

    
    @Override
    public CountResponseMessage processRequest(RequestMessage<UpdateRequestParameters> request) throws IOException, CommonException {
        try
        {
            RequestMessage<UpdateRequestParameters> req = (RequestMessage<UpdateRequestParameters>)request;
           
            IEntityManager em = EntityManagerStorage.getInstance().getEntityManager(req.entityName);
            
            em.update(req.data.entities);

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
