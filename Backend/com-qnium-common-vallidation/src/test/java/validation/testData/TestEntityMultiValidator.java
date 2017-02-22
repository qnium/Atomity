/*
 * This file is copyrighted by QNIUM LLC
 * And licensed on eclusive/non-exclusive basis under the terms of the license given to the final user 
 * or in terms of the contract concluded between sources licensee and QNIUM LLC
 */
package validation.testData;

import com.qnium.common.validation.FieldValidator;
import com.qnium.common.validation.basic.ValidateMax;
import com.qnium.common.validation.basic.ValidateRequired;

/**
 *
 * @author nbv
 */
public class TestEntityMultiValidator {
    @FieldValidator(validator = ValidateRequired.class)
    @FieldValidator(validator = ValidateMax.class, param = "123", errorMessage = "Max value is: %d")
    public String testString;
}
