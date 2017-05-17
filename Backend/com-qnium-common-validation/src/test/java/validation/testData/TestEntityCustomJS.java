/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package validation.testData;

import com.qnium.common.validation.FieldValidator;
import com.qnium.common.validation.basic.ValidateScript;

/**
 *
 * @author nbv
 */
public class TestEntityCustomJS {
    @FieldValidator(validator = ValidateScript.class, param = "object == 'test'", errorMessage = "String should be set to 'test'")
    public String testData;
}
