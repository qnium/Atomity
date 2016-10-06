/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.backend.assets.dataobjects;

import java.util.List;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 *
 * @author Drozhin
 * @param <T>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateRequestParameters<T>
{
    public List<T> entities;
}
