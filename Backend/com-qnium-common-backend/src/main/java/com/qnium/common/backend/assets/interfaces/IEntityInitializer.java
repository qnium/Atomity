/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.backend.assets.interfaces;

import com.qnium.common.backend.core.EntityManager;

/**
 *
 * @author Drozhin
 */
public interface IEntityInitializer<T>
{
    void initialize(EntityManager<T> em) throws Exception;
}
