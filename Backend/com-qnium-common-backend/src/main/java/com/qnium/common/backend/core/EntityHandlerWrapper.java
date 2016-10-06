/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.backend.core;

import com.qnium.common.backend.assets.definitions.EntityHandlerType;
import com.qnium.common.backend.assets.interfaces.IEntityHandler;

/**
 *
 * @author Drozhin
 */
public class EntityHandlerWrapper
{
    public EntityHandlerType handlerType;
    public IEntityHandler handler;
}
