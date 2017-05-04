package com.qnium.atomitybackend;



import com.fasterxml.jackson.core.type.TypeReference;
import com.j256.ormlite.field.DataPersisterManager;
import com.qnium.atomitybackend.auth.AuthenticationManager;
import com.qnium.atomitybackend.auth.AuthorizationManager;
import com.qnium.atomitybackend.auth.DefaultAuthenticationHandler;
import com.qnium.atomitybackend.auth.definitions.AccountType;
import com.qnium.atomitybackend.auth.handlers.AuthenticationHandler;
import com.qnium.atomitybackend.auth.handlers.AuthenticationRequest;
import com.qnium.atomitybackend.users.data.User;
import com.qnium.atomitybackend.users.entityhandlers.ProtectShopPassword;
import com.qnium.common.backend.assets.dataobjects.CollectionResponseMessage;
import com.qnium.common.backend.assets.dataobjects.ObjectResponseMessage;
import com.qnium.common.backend.assets.dataobjects.ReadRequestParameters;
import com.qnium.common.backend.assets.dataobjects.RequestMessage;
import com.qnium.common.backend.assets.definitions.EntityHandlerType;
import com.qnium.common.backend.assets.handlers.generic.ReadHandler;
import com.qnium.common.backend.core.CommonValidationManager;
import com.qnium.common.backend.core.ConfigManager;
import com.qnium.common.backend.core.EntityManager;
import com.qnium.common.backend.core.EntityManagerStorage;
import com.qnium.common.backend.core.HandlerWrapper;
import com.qnium.common.backend.core.HandlersManager;
import com.qnium.common.backend.core.Logger;
import com.qnium.common.validation.ValidationManager;
import com.qnium.common.validation.handlers.ValidatorsHandler;
import java.sql.SQLException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ivan
 */
@WebListener
public class AppInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            registerOrmLiteTypes();
            ServletContext config = sce.getServletContext();
            ConfigManager cm = ConfigManager.getInstance();
            cm.setDatabaseDriverName(config.getInitParameter("databaseDriverName"));
            cm.setDatabaseURL(config.getInitParameter("databaseURL"));
            
            
            initEntityManagerStorage();
            initEntityHandlers();
            initHandlersManager(sce.getServletContext());
        }
        catch(Exception ex) {
            Logger.log.error("Initialization failed", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        Logger.log.info("Deinitializing backend...");
    }
    
    private static void initEntityManagerStorage() throws Exception {
        EntityManagerStorage entityManagerStorage = EntityManagerStorage.getInstance();
        entityManagerStorage.addEntityManager("users", EntityManager.getInstance(User.class));
    }
    
    static void initEntityHandlers() throws SQLException
    {     
        EntityManager<User> shopUserManager = EntityManager.getInstance(User.class);
        shopUserManager.addHandler(new ProtectShopPassword(), EntityHandlerType.BEFORE_CREATE_HANDLER);
        shopUserManager.addHandler(new ProtectShopPassword(), EntityHandlerType.BEFORE_SELF_CREATE_HANDLER);
    }
    
    private static void initHandlersManager(ServletContext config) throws Exception {
        HandlersManager hm = HandlersManager.getInstance();
        
        String action;
        
        CommonValidationManager.getInstance().setValidationProvider( object -> {
            ValidationManager.getInstance().validateObject(object);
        });
        ValidatorsHandler valhandler;
        
        action = "users.validators";
        valhandler = new ValidatorsHandler(User.class);
        hm.addHandler(action, valhandler.getWrapper());
        
        // Add other validation handlers here...

        action = "users.read";
        AuthorizationManager.getInstance().addPermission(AccountType.USER, action);
        ReadHandler usersReadHandler = new ReadHandler();
        
        hm.addHandler(action, new HandlerWrapper(
                new TypeReference<RequestMessage<ReadRequestParameters>>() {},
                new TypeReference<CollectionResponseMessage>() {},
                usersReadHandler));

        action = "auth.login";
        hm.addHandler(action, new HandlerWrapper (
                new TypeReference<RequestMessage<AuthenticationRequest>>() {},
                new TypeReference<ObjectResponseMessage<String>>() {},
                new AuthenticationHandler()));
        
        AuthenticationManager.getInstance().addAuthenticationHandler(AccountType.USER,
            new DefaultAuthenticationHandler());
        
        // Add other handlers here...
    }
    
    private void registerOrmLiteTypes() {
        DataPersisterManager.registerDataPersisters(new InstantTypePersister());
    }
    
}