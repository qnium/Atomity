/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.atomitybackend.users.initializers;

import com.j256.ormlite.dao.Dao;
import com.qnium.atomitybackend.auth.PasswordProtector;
import com.qnium.atomitybackend.users.data.User;
import com.qnium.common.backend.assets.interfaces.IEntityInitializer;
import com.qnium.common.backend.core.EntityManager;

/**
 *
 * @author Ivan
 */
public class UsersInitializer implements IEntityInitializer{

    @Override
    public void initialize(EntityManager em) throws Exception
    {
        EntityManager<User> usersEM = EntityManager.getInstance(User.class);
        
        User user = new User();
        user.email = "test@example.com";
        user.password = PasswordProtector.protectPassword("testpass");
        usersEM.create(user);
    }    
}
