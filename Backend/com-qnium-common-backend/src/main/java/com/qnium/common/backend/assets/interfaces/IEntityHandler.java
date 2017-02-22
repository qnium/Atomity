/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.backend.assets.interfaces;

import com.qnium.common.backend.exceptions.CommonException;

/**
 *
 * @author Drozhin
 * @param <T>
 */
public interface IEntityHandler<T>
{
    void run(T entity) throws CommonException;
}
