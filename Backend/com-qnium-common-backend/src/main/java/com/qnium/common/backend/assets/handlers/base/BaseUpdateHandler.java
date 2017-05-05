/*
 * This file is copyrighted by QNIUM LLC
 * And licensed on eclusive/non-exclusive basis under the terms of the license given to the final user 
 * or in terms of the contract concluded between sources licensee and QNIUM LLC
 */
package com.qnium.common.backend.assets.handlers.base;

import com.qnium.common.backend.assets.dataobjects.CountResponseMessage;
import com.qnium.common.backend.assets.dataobjects.RequestMessage;
import com.qnium.common.backend.assets.dataobjects.UpdateRequestParameters;
import com.qnium.common.backend.exceptions.CommonException;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Kirill Zhukov
 */
abstract public class BaseUpdateHandler extends AbstractBaseHandler<RequestMessage<List>, CountResponseMessage> {
    
    @Override
    abstract public CountResponseMessage processRequest(RequestMessage<List> request) throws IOException, CommonException;
    
}
