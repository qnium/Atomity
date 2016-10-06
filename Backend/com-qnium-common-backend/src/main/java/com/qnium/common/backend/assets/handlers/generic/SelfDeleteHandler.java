/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.backend.assets.handlers.generic;

import com.qnium.common.backend.assets.dataobjects.CountResponseMessage;
import com.qnium.common.backend.assets.dataobjects.DeleteRequestParameters;
import com.qnium.common.backend.assets.dataobjects.RequestMessage;
import com.qnium.common.backend.assets.interfaces.IEntityManager;
import com.qnium.common.backend.assets.interfaces.IHandler;
import com.qnium.common.backend.core.AuthManager;
import com.qnium.common.backend.core.EntityManagerStorage;
import com.qnium.common.backend.core.Logger;
import com.qnium.common.backend.exceptions.CommonException;
import java.io.IOException;

/**
 *
 * @author Drozhin
 */
public class SelfDeleteHandler implements IHandler<RequestMessage<DeleteRequestParameters>, CountResponseMessage>
{
    private final String accountIdField;
    private final String accountTypeField;

    public SelfDeleteHandler(String accountIdField){
        this.accountIdField = accountIdField;
        this.accountTypeField = null;
    }
    
    public SelfDeleteHandler(String accountIdField, String accountTypeField){
        this.accountIdField = accountIdField;
        this.accountTypeField = accountTypeField;
    }

    @Override
    public CountResponseMessage process(RequestMessage<DeleteRequestParameters> request) throws IOException, CommonException
    {
        CountResponseMessage response = new CountResponseMessage(0);
        
        try
        {
            long accountId = AuthManager.getInstance().getAccountId(request.sessionKey);
            int accountType = 0;
            if(accountTypeField != null && !accountTypeField.isEmpty()){
                accountType = AuthManager.getInstance().getAccountType(request.sessionKey);
            }
            
            IEntityManager em = EntityManagerStorage.getInstance()
                    .getEntityManager(request.entityName);
            
            em.selfDelete(request.data.entities, accountIdField, accountId, accountTypeField, accountType);
            
            response = new CountResponseMessage(em.getCount());
        }
        catch (CommonException ex) {
            throw ex;
        }
        catch (Exception ex) {
            response.error = "Error at self update.";
            Logger.log.error("Self delete error.", ex);
        }
        
        return response;
    }    
}
