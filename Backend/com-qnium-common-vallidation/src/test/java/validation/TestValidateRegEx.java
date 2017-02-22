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
import validation.testData.TestEntityRegEx;

/**
 *
 * @author nbv
 */
public class TestValidateRegEx {
    
    public TestValidateRegEx() {
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

    @Test(expected = ValidationException.class)
    public void validateRegEx() throws ValidationException
    {
        TestEntityRegEx entity = new TestEntityRegEx();
        entity.email = "test@";
        entity.www = "qnium";
        ValidationManager.getInstance().validateObject(entity);
    }
    
    @Test
    public void validateRegExOK() throws ValidationException
    {
        TestEntityRegEx entity = new TestEntityRegEx();
        entity.email = "test@test.com";
        entity.www = "http://www.qnium.com";
        ValidationManager.getInstance().validateObject(entity);
        entity.www = "www.qnium.com";
        ValidationManager.getInstance().validateObject(entity);
    }
    
    @Test
    public void ValidateEmailJS() throws Exception
    {
        String result = JSValidationTester.getInstance().validateData(TestEntityRegEx.class, "email", "test@");
        assertNotNull(result);
        result = JSValidationTester.getInstance().validateData(TestEntityRegEx.class, "email", "test@test.com");
        assertNull(result);
    }
    
   
    @Test
    public void ValidateWebJS() throws Exception
    {
        String result = JSValidationTester.getInstance().validateData(TestEntityRegEx.class, "www", "qnium");
        assertNotNull(result);
        result = JSValidationTester.getInstance().validateData(TestEntityRegEx.class, "www", "http://qnium.com");
        assertNull(result);
    }
}
