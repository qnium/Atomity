/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.backend.assets.dataobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author Drozhin
 * @param <T>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenericRequestParameters<T>
{
    public T value;
}
