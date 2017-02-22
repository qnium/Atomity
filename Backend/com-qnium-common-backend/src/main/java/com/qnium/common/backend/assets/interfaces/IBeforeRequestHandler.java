/*
 * This file is copyrighted by QNIUM LLC
 * And licensed on eclusive/non-exclusive basis under the terms of the license given to the final user 
 * or in terms of the contract concluded between sources licensee and QNIUM LLC
 */
package com.qnium.common.backend.assets.interfaces;

import com.qnium.common.backend.exceptions.CommonException;

/**
 *
 * @author Kirill Zhukov
 */
public interface IBeforeRequestHandler<T> {
    public T process(T request) throws CommonException;
}
