/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package validation;

import validation.testData.TestEntityMax;
import com.qnium.common.validation.exceptions.ValidationException;
import com.qnium.common.validation.ValidationManager;
import com.qnium.common.validation.data.ValidatorItem;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import validation.common.JSValidationTester;

/**
 *
 * @author nbv
 */
public class TestValidationMax {
    
    
    public TestValidationMax() {
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
    @Test(expected=ValidationException.class)
    public void ValidationString() throws ValidationException {
        TestEntityMax entity = new TestEntityMax();
        entity.testString = "125"; //Should fail
        ValidationManager.getInstance().validateObject(entity);
    }
    
    @Test(expected=ValidationException.class)
    public void ValidationInt() throws ValidationException 
    {
        TestEntityMax entity = new TestEntityMax();
        entity.testInt = 515;
        ValidationManager.getInstance().validateObject(entity);
    }
    
    @Test
    public void ValidationNormal() throws ValidationException 
    {
        TestEntityMax entity = new TestEntityMax();
        entity.testString = "122";
        entity.testInt = 510;
        ValidationManager.getInstance().validateObject(entity);
    }
    
    @Test
    public void ClientSideValidation() throws Exception
    {
        String result = JSValidationTester.getInstance().validateData(TestEntityMax.class, "testString", "122");
        assertNull(result);
        result = JSValidationTester.getInstance().validateData(TestEntityMax.class, "testString", "124");
        assertNotNull(result);
    }
}
