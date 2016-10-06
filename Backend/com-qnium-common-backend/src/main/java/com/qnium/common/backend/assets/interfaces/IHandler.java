/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.qnium.common.backend.assets.interfaces;

import com.qnium.common.backend.exceptions.CommonException;
import java.io.IOException;

/**
 *
 * @author
 * @param <I> stands for Input (Request)
 * @param <O> stansd for Output (Response)
 */
public interface IHandler<I, O>
{
    O process(I request) throws IOException, CommonException;
}
