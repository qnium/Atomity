/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.backend.assets.interfaces;

import com.qnium.common.backend.assets.dataobjects.CommonHandlerRequest;
import com.qnium.common.backend.assets.dataobjects.CommonHandlerResponse;
import com.qnium.common.backend.exceptions.CommonException;

/**
 *
 * @author DEV008
 */
public interface ICommonResponseHandler<T> {
    CommonHandlerResponse<T> process(CommonHandlerRequest<T> requestData, CommonHandlerResponse<T> responseData) throws CommonException;
}
