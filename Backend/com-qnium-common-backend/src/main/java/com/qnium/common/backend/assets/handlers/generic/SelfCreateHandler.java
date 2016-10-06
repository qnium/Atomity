/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.backend.assets.handlers.generic;

import com.qnium.common.backend.assets.dataobjects.CountResponseMessage;
import com.qnium.common.backend.assets.dataobjects.CreateRequestParameters;
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
public class SelfCreateHandler implements IHandler<RequestMessage<CreateRequestParameters>, CountResponseMessage>
{
    private final String accountIdField;
    private final String accountTypeField;

    public SelfCreateHandler(String accountIdField){
        this.accountIdField = accountIdField;
        this.accountTypeField = null;
    }
    
    public SelfCreateHandler(String accountIdField, String accountTypeField){
        this.accountIdField = accountIdField;
        this.accountTypeField = accountTypeField;
    }

    @Override
    public CountResponseMessage process(RequestMessage<CreateRequestParameters> request) throws IOException, CommonException
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
            
            em.selfCreate(request.data.entity, accountIdField, accountId, accountTypeField, accountType);
            
            response = new CountResponseMessage(em.getCount());
        } catch (CommonException ex){
            throw ex;
        } catch (Exception ex) {
            response.error = "Error at self create.";
            Logger.log.error("Self create error.", ex);
        }
        
        return response;
    }
}
