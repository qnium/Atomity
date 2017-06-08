/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.backend.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qnium.common.backend.exceptions.CommonException;
import java.time.Instant;

/**
 *
 * @author
 */
public class FieldFilter<T>
{
    public String field;
    public String operation;
    public T value;
    
    public FieldFilter()
    {
    }
    
    @JsonCreator
    public FieldFilter(
            @JsonProperty("field")String field,
            @JsonProperty("operation")String operation,
            @JsonProperty("value")T value)
    {
        this.field = field;
        this.operation = operation;
        this.value = value;
    }
    
    @JsonIgnore
    public Integer getIntValue() throws CommonException {
        if (value instanceof Integer) {
            return (Integer)value;
        }
        else if (value instanceof String) {
            try {
                return Integer.parseInt(value.toString());
            }
            catch (NumberFormatException ex) {
                String msg = String.format(
                    "String value [%s] for filter %s can't be parsed as integer",
                    value, field);
                throw new CommonException(0, msg, ex);
            }
        }
        else {
            String msg = String.format(
                "Value [%s] for filter %s can't be converted to integer",
                value, field);
            throw new CommonException(0, msg);
        }
    }
    
    @JsonIgnore
    public Instant getDateTimeValue() throws CommonException {
        if (value instanceof String) {
            try {
                return Instant.parse(value.toString());
            }
            catch (NumberFormatException ex) {
                String msg = String.format(
                    "String value [%s] for filter %s can't be parsed as date/time",
                    value, field);
                throw new CommonException(0, msg, ex);
            }
        }
        else if (value instanceof Number) {
            return Instant.ofEpochMilli((Long)this.value);
        }
        else {
            String msg = String.format(
                "Value [%s] for filter %s can't be converted to date/time",
                value, field);
            throw new CommonException(0, msg);
        }
    }
    
    @JsonIgnore
    public Boolean getBooleanValue() throws CommonException {
        if (value instanceof Boolean) {
            return (Boolean)value;
        }
        else if (value instanceof String) {
            return Boolean.valueOf(value.toString());
        }
        else {
            String msg = String.format(
                "Value [%s] for filter %s can't be converted to boolean",
                value, field);
            throw new CommonException(0, msg);
        }
    }
}
