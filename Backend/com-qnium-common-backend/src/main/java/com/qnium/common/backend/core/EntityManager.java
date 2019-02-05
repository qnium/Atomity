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
import com.qnium.common.backend.assets.interfaces.IEntityHandler;
import com.qnium.common.backend.assets.interfaces.IEntityManager;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import com.qnium.common.backend.assets.interfaces.IEntityInitializer;
import com.qnium.common.backend.exceptions.CommonException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;

/**
 *
 * @author Drozhin
 * @param <T>
 */
public class EntityManager<T> implements IEntityManager<T>
{    
    private static final HashMap<Class, EntityManager> instances = new HashMap<>();
    private final Dao<T, Long> genericDao;
    private final List<EntityHandlerWrapper> handlers;

    public static synchronized <T> EntityManager<T> getInstance(Class<T> t) throws SQLException
    {                
        if(!instances.containsKey(t)) {
            instances.put(t, new EntityManager(t));
        }
        
        return instances.get(t);
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
    
    /**
     * Creates a {@link Where} condition that combines list of given conditions 
     * using "OR" operation. All conditions must be created using the same
     * {@link Where} instance.
     * @param wh Where instance used for creating conditions.
     * @param conditions List of conditions to combine.
     * @return Combined condition
     */
    private Where<T, Long> combineWithOr(Where<T, Long> wh, List<Where<T, Long>> conditions) {
        int orSize = conditions.size();
        Where<T, Long> result;
        if (orSize > 1) {
            Where<T, Long> firstCond = conditions.get(0);
            Where<T, Long> secondCond = conditions.get(1);
            if (orSize > 2) {
                // Calling newInstance is required to create array of generic type
                Where<T, Long>[] restConditions = (Where<T, Long>[])Array.newInstance(firstCond.getClass(), orSize - 2);
                for (int i = 0; i < orSize - 2; i++) {
                    restConditions[i] = conditions.get(i + 2);
                }
                result = wh.or(firstCond, secondCond, restConditions);
            }
            else {
                result = wh.or(firstCond, secondCond);
            }
        }
        else if (orSize > 0) {
            result = conditions.get(0);
        }
        else {
            return wh.raw("1 = 1");
        }
        return result;
    }
    
    /**
     * Creates a {@link Where} condition that combines list of given conditions 
     * using "AND" operation. All conditions must be created using the same
     * {@link Where} instance.
     * @param wh Where instance used for creating conditions.
     * @param conditions List of conditions to combine.
     * @return Combined condition
     */
    private Where<T, Long> combineWithAnd(Where<T, Long> wh, List<Where<T, Long>> conditions) {
        int andSize = conditions.size();
        Where<T, Long> result;
        if (andSize > 1) {
            Where<T, Long> firstCond = conditions.get(0);
            Where<T, Long> secondCond = conditions.get(1);
            if (andSize > 2) {
                // Calling newInstance is required to create array of generic type
                Where<T, Long>[] restConditions = (Where<T, Long>[])Array.newInstance(firstCond.getClass(), andSize - 2);
                for (int i = 0; i < andSize - 2; i++) {
                    restConditions[i] = conditions.get(i + 2);
                }
                result = wh.and(firstCond, secondCond, restConditions);
            }
            else {
                result = wh.and(firstCond, secondCond);
            }
        }
        else if (andSize > 0) {
            result = conditions.get(0);
        }
        else {
            result = wh.raw("1 = 1");
        }
        return result;
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
                Where<T, Long> genericWH = genericQB.where();
                List<Where<T, Long>> whereConditions = new ArrayList<>();
                Date dateToCompare;
                Calendar calendar;
                for (FieldFilter f : fieldFilters) {
                    if (f.value != null && !f.value.toString().isEmpty())
                    {                        
                        String[] fieldNames = f.field.split("\\|");
                        switch (f.operation) {
                            case FieldOperations.EQ:
                                List<Where<T, Long>> eqCondList = new ArrayList<>();
                                for (String name : fieldNames) {
                                    eqCondList.add(genericWH.eq(name, f.value));
                                }
                                whereConditions.add(combineWithOr(genericWH, eqCondList));
                                break;
                            case FieldOperations.NE:
                                whereConditions.add(genericWH.ne(f.field, f.value));
                                break;
                            case FieldOperations.NOT_NULL:
                                whereConditions.add(genericWH.isNotNull(f.field));
                                break;
                            case FieldOperations.LIKE:
                                String likeValue = "%" + f.value + "%";
                                List<Where<T, Long>> likeCondList = new ArrayList<>();
                                for (String name : fieldNames) {
                                    likeCondList.add(genericWH.like(name, likeValue));
                                }
                                whereConditions.add(combineWithOr(genericWH, likeCondList));
                                break;
                            case FieldOperations.DATE_LE:
                                Instant maxInstant;
                                try{
                                    maxInstant = OffsetDateTime.parse(f.value.toString()).toInstant();
                                } catch (Exception ex) {
                                    Logger.log.warn(
                                        "Datetime filter value [" +
                                        f.value +
                                        "] is incorrect, applying default max datetime filter",
                                        ex);
                                    maxInstant = LocalDate.now()
                                        .atTime(LocalTime.MIDNIGHT)
                                        .plus(1, ChronoUnit.DAYS)
                                        .minus(1, ChronoUnit.NANOS)
                                        .toInstant(ZoneOffset.UTC);
                                }
                                whereConditions.add(genericWH.le(f.field, maxInstant));
                                break;
                            case FieldOperations.DATE_GE:
                                Instant minInstant;
                                try{
                                    minInstant = OffsetDateTime.parse(f.value.toString()).toInstant();
                                } catch (Exception ex) {
                                    Logger.log.warn(
                                        "Datetime filter value [" +
                                        f.value +
                                        "] is incorrect, applying default min datetime filter",
                                        ex);
                                    minInstant = LocalDate.now()
                                        .atTime(LocalTime.MIDNIGHT)
                                        .toInstant(ZoneOffset.UTC);
                                }
                                whereConditions.add(genericWH.ge(f.field, minInstant));
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
                                whereConditions.add(genericWH.in(f.field, values));
                                break;
                            case FieldOperations.NOT_IN:
                                List excludedValues = (List)f.value;
                                if(!excludedValues.isEmpty())
                                {
                                    whereConditions.add(genericWH.not().in(f.field, excludedValues));
                                }
                                break;
                            case FieldOperations.LE:
                                whereConditions.add(genericWH.le(f.field, f.value));
                                break;
                            case FieldOperations.GE:
                                whereConditions.add(genericWH.ge(f.field, f.value));
                                break;
                        }
                    }
                }
                combineWithAnd(genericWH, whereConditions);
            }
        }
            
        FilteredResult<T> result = new FilteredResult<>();
        result.totalCounter = genericQB.countOf();
        genericQB.setCountOf(false);

        if (count > 0) {
            genericQB = genericQB.offset(startIndex).limit(count);
        }
        
        String statement = genericQB.prepareStatementString();
        statement = "";
        
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
    
    public void runHandlers(T entity, EntityHandlerType handlerType) throws CommonException
    {
        if(entity == null){
            return;
        }
        
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
