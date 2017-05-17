/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.validation;

import com.qnium.common.validation.exceptions.ValidationException;

/**
 *
 * @author nbv
 */
public interface IValidator {
    public void init(String params, String errorMessage);
    public void validate(String object) throws ValidationException;
    public String getJSCode();
    public String getError();
}
