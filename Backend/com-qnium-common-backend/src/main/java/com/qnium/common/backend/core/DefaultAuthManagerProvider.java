/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.backend.core;

import com.qnium.common.backend.assets.interfaces.IAuthManagerProvider;

/**
 *
 * @author Drozhin
 */
public class DefaultAuthManagerProvider implements IAuthManagerProvider
{
    //String errMessage = "Default auth provider not supported verification.";
    //Defualt will allow everything
    
    @Override
    public void checkPermission(String session, String entity, String action) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public long getAccountId(String sessionKey) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public int getAccountType(String sessionKey) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
