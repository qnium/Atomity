/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.backend.core;

import java.util.HashMap;
import com.qnium.common.backend.assets.interfaces.IEntityManager;

/**
 *
 * @author
 */
public class EntityManagerStorage {
    private static EntityManagerStorage _instance = null;
    private final HashMap<String, IEntityManager> storage;
    
    public static synchronized EntityManagerStorage getInstance()
    {
        if ( _instance == null )
        {
            _instance = new EntityManagerStorage();
        }
        
        return _instance;
    }
    
    private EntityManagerStorage() {
        storage = new HashMap<>();
    }
    
    public IEntityManager getEntityManager(String entity) {
        return storage.get(entity);
    }
    
    public void addEntityManager(String entityName, IEntityManager entityManager) throws Exception{
        if(!storage.containsKey(entityName)) {
            storage.put(entityName, entityManager);
        } else {
            throw new Exception(String.format("Entity \"%s\" already exists in storage", entityName));
        }
    }
}
