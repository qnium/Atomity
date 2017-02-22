/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.qnium.common.backend.assets.dataobjects;

import java.util.List;

/**
 *
 * @author
 * @param <T>
 */
public class CollectionResponseMessage<T> extends ResponseMessage{
    public long totalCounter;
    public List<T> data;
}
