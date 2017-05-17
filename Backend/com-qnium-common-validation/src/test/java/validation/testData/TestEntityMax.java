/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package validation.testData;

import com.qnium.common.validation.FieldValidator;
import com.qnium.common.validation.basic.ValidateMax;

/**
 *
 * @author nbv
 */
public class TestEntityMax {
    @FieldValidator(validator = ValidateMax.class, param = "123", errorMessage = "Max value is: \"%d\"")
    public String testString;
    @FieldValidator(validator = ValidateMax.class, param = "511", errorMessage = "Max value is: %d")
    public int testInt;
}
