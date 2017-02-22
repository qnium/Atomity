/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.backend.assets.interfaces;

import com.j256.ormlite.dao.Dao;
import com.qnium.common.backend.core.FieldFilter;
import com.qnium.common.backend.core.FilteredResult;
import java.util.List;

/**
 *
 * @author
 * @param <T>
 */
public interface IEntityManager<T> {
    void create(T entity) throws Exception;
    void selfCreate(T entity, String accountIdField, long accountId, String accountTypeField, int accountType) throws Exception;
    FilteredResult<T> read(List<FieldFilter> filters, long startIndex, long count) throws Exception;
    FilteredResult<T> selfRead(List<FieldFilter> filters, long startIndex, long count,
            String accountField, long accountId,
            String accountTypeField, int accountType) throws Exception;
    void update(List<T> entities) throws Exception;
    void selfUpdate(List<T> entities, String accountIdField, long accountId, String accountTypeField, int accountType) throws Exception;
    void delete(List<T> entities) throws Exception;
    void selfDelete(List<T> entities, String accountIdField, long accountId, String accountTypeField, int accountType) throws Exception;
    long getCount() throws Exception;
    Dao<T, Long> getDao();
}
