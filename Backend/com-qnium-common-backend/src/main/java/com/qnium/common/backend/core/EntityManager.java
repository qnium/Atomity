/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.backend.core;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.qnium.common.backend.assets.definitions.EntityInitializer;
import com.qnium.common.backend.assets.definitions.EntityHandlerType;
import com.qnium.common.backend.assets.definitions.FieldOperations;
import com.qnium.common.backend.assets.interfaces.IContextValueProvider;
import com.qnium.common.backend.assets.interfaces.IEntityHandler;
import com.qnium.common.backend.assets.interfaces.IEntityManager;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.bind.DatatypeConverter;
import com.qnium.common.backend.assets.interfaces.IEntityInitializer;
import com.qnium.common.backend.exceptions.CommonException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;

/**
 *
 * @author Drozhin
 * @param <T>
 */
public class EntityManager<T> implements IEntityManager<T>
{   
    static final class EntityInstance { Class entityClass; EntityManager em; };
    
    
    //private static final HashMap<Class, EntityManager> instances = new HashMap<>();
    private static final List<EntityInstance> instances = new ArrayList<>();
    private final Dao<T, Long> genericDao;
    private final List<EntityHandlerWrapper> handlers;
    private final ContextKey[] contexts;
    /*
    public boolean validContext(ContextKey[] new_contexts)
    {
        for ( ContextKey context : new_contexts )
        {
            //if (con)
        }
    }
    */

    public static synchronized <T> EntityManager<T> getInstance(Class<T> t, ContextKey[] contexts) throws SQLException
    {   
        
        /*
        if(!instances.containsKey(t)) {
            instances.put(t, new EntityManager(t));
        }
        
        return instances.get(t);
        */
        instances.stream().filter( instance -> instance.entityClass == t && instance.em.contexts == contexts).findFirst();
        
        
    }
    
    private <T1> EntityManager(Class<T> t) throws SQLException
    {        
        String databaseUrl = ConfigManager.getInstance().getDatabaseURL();
        ConnectionSource connectionSource = new JdbcConnectionSource(databaseUrl);
        
        handlers = new ArrayList();        
        
        genericDao = DaoManager.createDao(connectionSource, t);
        if (!genericDao.isTableExists())
        {
            TableUtils.createTable(connectionSource, t);
            
            EntityInitializer annotation = t.getAnnotation(EntityInitializer.class);            
            if(annotation != null){
                try{
                    ((IEntityInitializer)annotation.entityInitializerClass().newInstance()).initialize(this);
                } catch (Exception ex) {
                    throw new SQLException(ex);
                }
            }
        }
        
        new Timer().scheduleAtFixedRate(new ConnectionUpdater(genericDao), 1 * 60 * 60 * 1000, 1 * 60 * 60 * 1000);
    }
    
    @Override
    public void create(T entity) throws Exception {
        CommonValidationManager.getInstance().validatObject(entity);
        runHandlers(entity, EntityHandlerType.BEFORE_CREATE_HANDLER);
        genericDao.create(entity);
        runHandlers(entity, EntityHandlerType.AFTER_CREATE_HANDLER);
    }

    @Override
    public void selfCreate(T entity, String accountIdField, long accountId, String accountTypeField, int accountType) throws Exception
    {
        CommonValidationManager.getInstance().validatObject(entity);
        runHandlers(entity, EntityHandlerType.BEFORE_SELF_CREATE_HANDLER);
        
        genericDao.create(entity);
        
        long selfId = genericDao.extractId(entity);
        
        UpdateBuilder<T, Long> updateBuilder = genericDao.updateBuilder();
        updateBuilder.where().idEq(selfId);
        updateBuilder.updateColumnValue(accountIdField, accountId);
        if(accountTypeField != null && !accountTypeField.isEmpty()){
            updateBuilder.updateColumnValue(accountTypeField, accountType);
        }
        updateBuilder.update();        
        
        runHandlers(entity, EntityHandlerType.AFTER_SELF_CREATE_HANDLER);
    }
    
    private FilteredResult<T> internalRead(List<FieldFilter> filters, long startIndex, long count) throws SQLException, CommonException
    {
        QueryBuilder<T, Long> genericQB = genericDao.queryBuilder();
        
        if(filters != null && filters.size() > 0)
        {
            List<FieldFilter> sortFilters = filters.stream().filter(p -> p.operation.equals(FieldOperations.SORT)).collect(Collectors.toList());
            for (FieldFilter f : sortFilters)
            {
                try {
                    boolean ascending = (boolean)f.value;
                    genericQB.orderBy(f.field, ascending);
                } catch (Exception ex) {}
            }

            List<FieldFilter> distinctFilters = filters.stream().filter(p -> p.operation.equals(FieldOperations.DISTINCT)).collect(Collectors.toList());
            
            for (FieldFilter f : distinctFilters){
                if(f.field != null && !f.field.isEmpty()){
                    genericQB.distinct().selectColumns(f.field);
                }
            }
            
            List<FieldFilter> fieldFilters = filters.stream().filter(p -> !p.operation.equalsIgnoreCase(FieldOperations.SORT) && !p.operation.equalsIgnoreCase(FieldOperations.DISTINCT)).collect(Collectors.toList());

            if (fieldFilters.size() > 0)
            {
                Where<T, Long> genericWH = null;
                boolean isFirstCondition = true;
                Date dateToCompare;
                Calendar calendar;
                for (FieldFilter f : fieldFilters) {
                    if (f.value != null && !f.value.toString().isEmpty())
                    {                        
                        if(!isFirstCondition){
                            genericWH.and();
                        } else{
                            genericWH = genericQB.where();
                            isFirstCondition = false;
                        }
                        switch (f.operation) {
                            case FieldOperations.EQ:
                                genericWH.eq(f.field, f.value);
                                break;
                            case FieldOperations.LIKE:
                                genericWH.like(f.field, "%" + f.value + "%");
                                break;
                            case FieldOperations.DATE_LE:
                                try{
                                    dateToCompare = DatatypeConverter.parseDateTime(f.value.toString()).getTime();
                                } catch (Exception ex) {
                                    dateToCompare = new Date();
                                    dateToCompare.setTime((Long)f.value);
                                }
                                calendar = Calendar.getInstance();
                                calendar.setTime(dateToCompare);
                                calendar.set(Calendar.HOUR_OF_DAY, 23);
                                calendar.set(Calendar.MINUTE, 59);
                                calendar.set(Calendar.SECOND, 59);
                                calendar.set(Calendar.MILLISECOND, 999);
                                dateToCompare = calendar.getTime();
                                genericWH.le(f.field, dateToCompare);
                                break;
                            case FieldOperations.DATE_GE:
                                try{
                                    dateToCompare = DatatypeConverter.parseDateTime(f.value.toString()).getTime();
                                } catch (Exception ex) {
                                    dateToCompare = new Date();
                                    dateToCompare.setTime((Long)f.value);
                                }
                                calendar = Calendar.getInstance();
                                calendar.setTime(dateToCompare);
                                calendar.set(Calendar.HOUR_OF_DAY, 0);
                                calendar.set(Calendar.MINUTE, 0);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);
                                dateToCompare = calendar.getTime();
                                genericWH.ge(f.field, dateToCompare);
                                break;
                            case FieldOperations.IN:
                                List values = (List)f.value;
                                if(values.isEmpty())
                                {
                                    FilteredResult<T> result = new FilteredResult();
                                    result.totalCounter = 0;
                                    result.data = new ArrayList();
                                    return result;
                                }
                                genericWH.in(f.field, values);
                                
                        }
                    }
                }
            }
        }
            
        FilteredResult<T> result = new FilteredResult<>();
        result.totalCounter = genericQB.countOf();
        genericQB.setCountOf(false);

        if (count > 0) {
            genericQB = genericQB.offset(startIndex).limit(count);
        }
        
        result.data = genericQB.query();
        
        return result;
    }
    
    @Override
    public FilteredResult<T> read(List<FieldFilter> filters, long startIndex, long count) throws SQLException, CommonException
    {
        FilteredResult<T> result = internalRead(filters, startIndex, count);
        
        for(T entity : result.data)
            runHandlers(entity, EntityHandlerType.AFTER_READ_HANDLER);
        
        return result;
    }
    
    @Override
    public FilteredResult<T> selfRead(List<FieldFilter> filters, long startIndex, long count,
            String accountIdField, long accountId, String accountTypeField, int accountType) throws SQLException, CommonException
    {
            if(filters == null){
                filters = new ArrayList();
            }
            
            FieldFilter idFilter = new FieldFilter();
            idFilter.field = accountIdField;
            idFilter.operation = FieldOperations.EQ;
            idFilter.value = accountId;
            filters.add(idFilter);
            
            if (accountTypeField != null && !accountTypeField.isEmpty()) {
                FieldFilter typeFilter = new FieldFilter();
                typeFilter.field = accountTypeField;
                typeFilter.operation = FieldOperations.EQ;
                typeFilter.value = accountType;
                filters.add(typeFilter);
            }
            
        FilteredResult<T> result = internalRead(filters, startIndex, count);
        
        for(T entity : result.data)
            runHandlers(entity, EntityHandlerType.AFTER_SELF_READ_HANDLER);

        return result;
    }
        
        @Override
    public void update(List<T> entities) throws Exception {
         for(T entity : entities){
            CommonValidationManager.getInstance().validatObject(entity);
         }
        for(T entity : entities){
            runHandlers(entity, EntityHandlerType.BEFORE_UPDATE_HANDLER);
            genericDao.update(entity);
            runHandlers(entity, EntityHandlerType.AFTER_UPDATE_HANDLER);
        }            
    }

    @Override
    public void selfUpdate(List<T> entities, String accountIdField, long accountId, String accountTypeField, int accountType) throws Exception
    {
        for(T entity : entities){
            CommonValidationManager.getInstance().validatObject(entity);
         }
        
        for(T entity : entities)
        {
            long selfId = genericDao.extractId(entity);
            
            Where<T, Long> query = genericDao.queryBuilder().where()
                    .idEq(selfId)
                    .and()
                    .eq(accountIdField, accountId);
            
            if(accountTypeField != null && !accountTypeField.isEmpty()){
                query.and().eq(accountTypeField, accountType);
            }
            
            List<T> entitiesInDB = query.query();
            
            if(entitiesInDB.size() == 1)
            {
                runHandlers(entity, EntityHandlerType.BEFORE_SELF_UPDATE_HANDLER);                
                genericDao.update(entity);
                UpdateBuilder<T, Long> updateBuilder = genericDao.updateBuilder();
                updateBuilder.where().idEq(selfId);
                updateBuilder.updateColumnValue(accountIdField, accountId);
                if(accountTypeField != null && !accountTypeField.isEmpty()){
                    updateBuilder.updateColumnValue(accountTypeField, accountType);
                }
                updateBuilder.update();
                runHandlers(entity, EntityHandlerType.AFTER_SELF_UPDATE_HANDLER);
            }
        }
    }
    
    @Override
    public void delete(List<T> entities) throws Exception {
        for(T entity : entities){
            runHandlers(entity, EntityHandlerType.BEFORE_DELETE_HANDLER);
            genericDao.delete(entity);
            runHandlers(entity, EntityHandlerType.AFTER_DELETE_HANDLER);
        }            
    }

    @Override
    public void selfDelete(List<T> entities, String accountIdField, long accountId, String accountTypeField, int accountType) throws Exception
    {
        for(T entity : entities)
        {
            long selfId = genericDao.extractId(entity);
            
            QueryBuilder queryBuilder = genericDao.queryBuilder();
            
            Where<T, Long> query = queryBuilder.where()
                    .idEq(selfId)
                    .and()
                    .eq(accountIdField, accountId);
            
            if(accountTypeField != null && !accountTypeField.isEmpty()){
                query.and().eq(accountTypeField, accountType);
            }
            
            List<T> entitiesInDB = query.query();
            if(entitiesInDB.size() == 1)
            {
                runHandlers(entity, EntityHandlerType.BEFORE_SELF_DELETE_HANDLER);
                genericDao.delete(entity);
                runHandlers(entity, EntityHandlerType.AFTER_SELF_DELETE_HANDLER);
            }
        }
    }
    
    @Override
    public long getCount() throws Exception {
        return genericDao.countOf();
    }
    
    @Override
    public Dao<T, Long> getDao(){
        return genericDao;
    }    
    
    private void runHandlers(T entity, EntityHandlerType handlerType) throws CommonException
    {
        for(int i = 0; i < handlers.size(); i++)
        {
            EntityHandlerWrapper handler = handlers.get(i);
            
            if(handler.handlerType == handlerType){
                handler.handler.run(entity);
            }
        }
    }
    
    public void addHandler(IEntityHandler handler, EntityHandlerType type){
        EntityHandlerWrapper wrapper = new EntityHandlerWrapper();
        wrapper.handler = handler;
        wrapper.handlerType = type;                
        handlers.add(wrapper);
    }
    
    public T getEntityById(long id) throws SQLException, CommonException
    {
        T entity = genericDao.queryForId(id);
        runHandlers(entity, EntityHandlerType.AFTER_READ_HANDLER);
        return entity;
    }
    
    public List<T> getRelatedEntities(String fieldName, long foreignKey) throws SQLException, CommonException
    {
        List<T> entities = genericDao.queryForEq(fieldName, foreignKey);
        
        for(T entity : entities){
            runHandlers(entity, EntityHandlerType.AFTER_READ_HANDLER);
        }
        
        return entities;
    }
    
    public List<T> getFilteredEntities(Map<String, Object> filter) throws SQLException, CommonException
    {
        List<T> entities = genericDao.queryForFieldValues(filter);
        
        for(T entity : entities){
            runHandlers(entity, EntityHandlerType.AFTER_READ_HANDLER);
        }
        
        return entities;
    }
}
