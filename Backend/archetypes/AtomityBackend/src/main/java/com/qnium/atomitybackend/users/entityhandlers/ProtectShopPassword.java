/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.atomitybackend.users.entityhandlers;

import com.qnium.atomitybackend.auth.PasswordProtector;
import com.qnium.atomitybackend.users.data.User;
import com.qnium.common.backend.assets.interfaces.IEntityHandler;
import com.qnium.common.backend.exceptions.CommonException;


/**
 *
 * @author Drozhin
 */
public class ProtectShopPassword implements IEntityHandler<User>
{
    @Override
    public void run(User entity) throws CommonException {
        entity.password = PasswordProtector.protectPassword(entity.password);
    }    
}
