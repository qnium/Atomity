/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.backend.assets.dataobjects;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 *
 * @author Drozhin
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ByIdRequestParameters
{
    public long id;
}
