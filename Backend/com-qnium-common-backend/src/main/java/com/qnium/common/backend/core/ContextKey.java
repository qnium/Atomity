/*
 * This file is copyrighted by QNIUM LLC
 * And licensed on eclusive/non-exclusive basis under the terms of the license given to the final user 
 * or in terms of the contract concluded between sources licensee and QNIUM LLC
 */
package com.qnium.common.backend.core;

import com.qnium.common.backend.assets.interfaces.IContextValueProvider;

/**
 *
 * @author Kirill Zhukov
 */
public class ContextKey {
    public String FieldName;
    public IContextValueProvider provider;
}
