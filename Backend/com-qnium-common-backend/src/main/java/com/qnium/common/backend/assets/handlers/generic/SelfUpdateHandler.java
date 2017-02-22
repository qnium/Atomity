/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.backend.assets.handlers.generic;

import com.qnium.common.backend.assets.dataobjects.CollectionResponseMessage;
import com.qnium.common.backend.assets.dataobjects.RequestMessage;
import com.qnium.common.backend.assets.dataobjects.UpdateRequestParameters;
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
public class SelfUpdateHandler implements IHandler<RequestMessage<UpdateRequestParameters>, CollectionResponseMessage>
{
    private final String accountIdField;
    private final String accountTypeField;

    public SelfUpdateHandler(String accountIdField){
        this.accountIdField = accountIdField;
        this.accountTypeField = null;
    }
    
    public SelfUpdateHandler(String accountIdField, String accountTypeField){
        this.accountIdField = accountIdField;
        this.accountTypeField = accountTypeField;
    }
    
    @Override
    public CollectionResponseMessage process(RequestMessage<UpdateRequestParameters> request) throws IOException, CommonException
    {
        CollectionResponseMessage response = new CollectionResponseMessage();        
        
        try
        {
            long accountId = AuthManager.getInstance().getAccountId(request.sessionKey);
            int accountType = 0;
            if(accountTypeField != null && !accountTypeField.isEmpty()){
                accountType = AuthManager.getInstance().getAccountType(request.sessionKey);
            }
            
            EntityManagerStorage.getInstance()
                    .getEntityManager(request.entityName)
                    .selfUpdate(request.data.entities, accountIdField, accountId, accountTypeField, accountType);
            
        }
        catch (CommonException ex) {
            throw ex;
        }
        catch (Exception ex) {
            response.error = "Error at self update.";
            Logger.log.error("Self update error.", ex);
        }
        
        return response;
    }
}
