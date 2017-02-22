/*
 * This file is copyrighted by QNIUM LLC
 * And licensed on eclusive/non-exclusive basis under the terms of the license given to the final user 
 * or in terms of the contract concluded between sources licensee and QNIUM LLC
 */
package validation.testData;

import com.qnium.common.validation.FieldValidator;
import com.qnium.common.validation.basic.ValidateRegEx;
import com.qnium.common.validation.constant.RegExp;

/**
 *
 * @author nbv
 */
public class TestEntityRegEx {
    
    @FieldValidator(validator = ValidateRegEx.class, param = RegExp.EMAIL, errorMessage = "Invalid email value")
    public String email;
    @FieldValidator(validator = ValidateRegEx.class, param = RegExp.URL, errorMessage = "Invalid URL value")
    public String www;
}
