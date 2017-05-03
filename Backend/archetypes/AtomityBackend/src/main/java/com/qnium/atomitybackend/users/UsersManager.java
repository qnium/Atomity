/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.atomitybackend.users;

import com.j256.ormlite.dao.Dao;
import com.qnium.atomitybackend.common.definitions.ErrorCodes;
import com.qnium.atomitybackend.users.data.User;
import com.qnium.common.backend.core.EntityManager;
import com.qnium.common.backend.exceptions.CommonException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Ivan
 */
public class UsersManager {
    private static UsersManager instance;
    private static EntityManager<User> em;
    private static Dao<User, Long> usersDao;
    private final SecureRandom rng = new SecureRandom();

    public static synchronized UsersManager getInstance() throws SQLException
    {
        if (instance == null) {
            instance = new UsersManager();
        }
        return instance;
    }
    
    private UsersManager() throws SQLException
    {
        em = EntityManager.getInstance(User.class);
        usersDao = em.getDao();
    }
    
    public boolean userIsVerified(long userId) throws SQLException
    {
        return usersDao.queryForId(userId).verificationCode == null;
    }
    
    public void verifyUser(String verificationCode) 
            throws SQLException, CommonException
    {
        List<User> shopUsers = usersDao.
                queryForEq(User.VERIFICATION_CODE, verificationCode);
        if (shopUsers.isEmpty()) {
            throw new CommonException(ErrorCodes.GENERAL_ERROR, "Verification code is incorrect");
        }
        else {
            User shopUser = shopUsers.get(0);
            shopUser.verificationCode = null;
            usersDao.update(shopUser);
        }
    }

    public void verifyUser(long userId) throws SQLException {
        User user = usersDao.queryForId(userId);
        if (user != null) {
            user.verificationCode = null;
            usersDao.update(user);
        }
    }
}
