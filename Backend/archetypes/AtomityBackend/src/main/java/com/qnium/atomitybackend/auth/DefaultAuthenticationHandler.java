package com.qnium.atomitybackend.auth;


import com.qnium.atomitybackend.users.data.User;
import com.qnium.common.backend.core.EntityManager;
import com.qnium.atomitybackend.users.UsersManager;
import com.qnium.atomitybackend.common.definitions.ErrorCodes;
import com.qnium.atomitybackend.auth.exceptions.AuthenticationException;
import java.sql.SQLException;
import java.util.List;


public class DefaultAuthenticationHandler implements IAuthenticationHandler
{
    @Override
    public SessionContext authenticate(String login, String password) throws AuthenticationException {
        try {
            List<User> usersWithEmail = EntityManager
                .getInstance(User.class)
                .getDao()
                .queryForEq(User.EMAIL, login);
            
            if (usersWithEmail.isEmpty()) {
                throw new AuthenticationException(
                    ErrorCodes.AUTH_ERROR, 
                    "No user with email " + login);
            }
            
            User shopUser = usersWithEmail.get(0);
            
            if (!UsersManager.getInstance().userIsVerified(shopUser.id)) {
                throw new AuthenticationException(
                    ErrorCodes.ACCOUNT_NOT_VERIFIED,
                    "Email was not verified.");
            }
            
            if (PasswordProtector.checkPassword(password, shopUser.password)) {
                SessionContext context = SessionManager.getInstance().createSession();
                context.setUserId(shopUser.id);
                
                return context;
            }
            else {
                throw new AuthenticationException(ErrorCodes.AUTH_ERROR, "Wrong password!");
            }
        }
        catch(AuthenticationException authEx) {
            throw authEx;
        }
        catch(SQLException ex) {
            throw new AuthenticationException(ErrorCodes.GENERAL_ERROR, "Low level error");
        }
    }    
}
