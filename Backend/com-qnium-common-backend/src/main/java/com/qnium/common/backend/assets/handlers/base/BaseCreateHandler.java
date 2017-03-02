/*
 * This file is copyrighted by QNIUM LLC
 * And licensed on eclusive/non-exclusive basis under the terms of the license given to the final user 
 * or in terms of the contract concluded between sources licensee and QNIUM LLC
 */
package com.qnium.common.backend.assets.handlers.base;

import com.fasterxml.jackson.core.type.TypeReference;
import com.qnium.common.backend.assets.dataobjects.CollectionResponseMessage;
import com.qnium.common.backend.assets.dataobjects.CountResponseMessage;
import com.qnium.common.backend.assets.dataobjects.CreateRequestParameters;
import com.qnium.common.backend.assets.dataobjects.ReadRequestParameters;
import com.qnium.common.backend.assets.dataobjects.RequestMessage;
import com.qnium.common.backend.assets.interfaces.IAfterRequestHandler;
import com.qnium.common.backend.assets.interfaces.IBeforeRequestHandler;
import com.qnium.common.backend.assets.interfaces.IEntityManager;
import com.qnium.common.backend.assets.interfaces.IHandler;
import com.qnium.common.backend.core.HandlerWrapper;
import com.qnium.common.backend.exceptions.CommonException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kirill Zhukov
 */
public abstract class BaseCreateHandler extends AbstractBaseHandler<RequestMessage<CreateRequestParameters>, CountResponseMessage> {
    
   abstract public CountResponseMessage processRequest(RequestMessage<CreateRequestParameters> request) throws IOException, CommonException;

}
