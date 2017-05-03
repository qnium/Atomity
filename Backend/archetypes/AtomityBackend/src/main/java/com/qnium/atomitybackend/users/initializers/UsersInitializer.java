/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.atomitybackend.users.initializers;

import com.j256.ormlite.dao.Dao;
import com.qnium.atomitybackend.users.data.User;
import com.qnium.common.backend.assets.interfaces.IEntityInitializer;
import com.qnium.common.backend.core.EntityManager;
import com.qnium.common.backend.core.Logger;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Ivan
 */
public class UsersInitializer implements IEntityInitializer{

    @Override
    public void initialize(EntityManager em) throws Exception
    {
        EntityManager<User> usersEM = EntityManager.getInstance(User.class);
        Dao<User, Long> usersDao = usersEM.getDao();
        
        User user = new User();
        user.email = "test@example.com";
        user.password = "testpass";
        usersEM.create(user);
    }    
}
