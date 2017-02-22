/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.backend.assets.definitions;

/**
 *
 * @author Drozhin
 */
public enum EntityHandlerType
{
    BEFORE_CREATE_HANDLER,
    AFTER_CREATE_HANDLER,
    BEFORE_SELF_CREATE_HANDLER,
    AFTER_SELF_CREATE_HANDLER,
    BEFORE_UPDATE_HANDLER,
    AFTER_UPDATE_HANDLER,
    BEFORE_SELF_UPDATE_HANDLER,
    AFTER_SELF_UPDATE_HANDLER,
    BEFORE_DELETE_HANDLER,
    AFTER_DELETE_HANDLER,
    BEFORE_SELF_DELETE_HANDLER,
    AFTER_SELF_DELETE_HANDLER,
    AFTER_READ_HANDLER,
    AFTER_SELF_READ_HANDLER
}
