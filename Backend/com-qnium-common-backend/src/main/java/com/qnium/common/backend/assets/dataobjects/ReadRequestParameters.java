/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.backend.assets.dataobjects;

import com.qnium.common.backend.core.FieldFilter;
import java.util.List;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 *
 * @author Drozhin
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReadRequestParameters
{
    public long startIndex;
    public long count;
    public List<FieldFilter>filter;
}
