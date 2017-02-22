/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.backend.assets.interfaces;

/**
 *
 * @author
 */
public interface IAuthManagerProvider
{    
    //void verifyAdminPrivileges(String sessionKey) throws Exception;
    void checkPermission(String session, String entity, String action) throws Exception;
    long getAccountId(String sessionKey) throws Exception;
    int getAccountType(String sessionKey) throws Exception;
}
