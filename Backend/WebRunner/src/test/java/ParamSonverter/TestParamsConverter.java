/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ParamSonverter;

import com.qnium.webrunner.helpers.ParamsConverter;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author admin
 */
public class TestParamsConverter {
    
    public TestParamsConverter() {
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
    @Test
    public void SimpleTest() throws Exception {
        
        Map<String, String[]> data = new HashMap();
        
        data.put("name", new String [] {"OK"});
        data.put("id", new String [] {"2"});
        data.put("things", new String [] {"OK1", "OK2", "OK3"});
        data.put("list", new String[] {"0", "1", "2"});
        data.put("isThere", new String[] {"true"});
        
        TestParams params = (TestParams) ParamsConverter.convert(data, TestParams.class);
        assertEquals(params, "OK");
    }
}
