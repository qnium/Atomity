/*
 * This file is copyrighted by QNIUM LLC
 * And licensed on eclusive/non-exclusive basis under the terms of the license given to the final user 
 * or in terms of the contract concluded between sources licensee and QNIUM LLC
 */
package com.qnium.common.backend.assets.handlers.base;

import com.qnium.common.backend.assets.dataobjects.CountResponseMessage;
import com.qnium.common.backend.assets.dataobjects.DeleteRequestParameters;
import com.qnium.common.backend.assets.dataobjects.RequestMessage;
import com.qnium.common.backend.exceptions.CommonException;
import java.io.IOException;

/**
 *
 * @author nbv
 */
public abstract class BaseDeleteHandler extends AbstractBaseHandler<RequestMessage<DeleteRequestParameters>, CountResponseMessage> {

    @Override
    abstract public CountResponseMessage processRequest(RequestMessage<DeleteRequestParameters> request) throws IOException, CommonException;
    
}
