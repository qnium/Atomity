/*
 * This file is copyrighted by QNIUM LLC
 * And licensed on eclusive/non-exclusive basis under the terms of the license given to the final user 
 * or in terms of the contract concluded between sources licensee and QNIUM LLC
 */
package validation;

import com.qnium.common.validation.ValidationManager;
import com.qnium.common.validation.exceptions.ValidationException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import validation.common.JSValidationTester;
import validation.testData.TestEntityMax;
import validation.testData.TestEntityMultiValidator;

/**
 *
 * @author nbv
 */
public class TestMultipleValidators {
    
    public TestMultipleValidators() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test(expected=ValidationException.class)
    public void testMultipleValidation() throws ValidationException
    {
        boolean valRequired = false;
        TestEntityMultiValidator entity = new TestEntityMultiValidator();
        try{
            ValidationManager.getInstance().validateObject(entity);
        } catch(ValidationException ex)
        {
            valRequired = true;
        }
        
        assertTrue(valRequired);
        
        entity.testString = "124";
        ValidationManager.getInstance().validateObject(entity);;
        
    }
    
    @Test
    public void ClientSideValidation() throws Exception
    {
        String result = JSValidationTester.getInstance().validateData(TestEntityMultiValidator.class, "testString", "");
        assertNotNull(result);
        result = JSValidationTester.getInstance().validateData(TestEntityMultiValidator.class, "testString", "124");
        assertNotNull(result);
        result = JSValidationTester.getInstance().validateData(TestEntityMax.class, "testString", "123");
        assertNull(result);
    }
}
