package validation;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import com.qnium.common.validation.ValidationManager;
import com.qnium.common.validation.exceptions.ValidationException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import validation.testData.TestEntityCustomJS;

/**
 *
 * @author nbv
 */
public class TestCustomJS {
    
    public TestCustomJS() {
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

    @Test
    public void validatCustomJS() throws ValidationException {
        TestEntityCustomJS entity = new TestEntityCustomJS();
        entity.testData = "test";
        ValidationManager.getInstance().validateObject(entity);
        entity.testData = "testWrong";
        boolean wrong = false;
        try{
            ValidationManager.getInstance().validateObject(entity);
        } catch(ValidationException ex)
        {
            wrong = true;
        }
        
        assertTrue(wrong);
    }
}
