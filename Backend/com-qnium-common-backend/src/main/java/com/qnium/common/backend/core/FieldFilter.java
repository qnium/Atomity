/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.backend.core;

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
    
    public FieldFilter(String field, String operation, T value)
    {
        this.field = field;
        this.operation = operation;
        this.value = value;
    }
}
