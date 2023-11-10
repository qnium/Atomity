/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.qnium.common.backend.assets.dataobjects.CollectionResponseMessage;
import com.qnium.common.backend.assets.dataobjects.ReadRequestParameters;
import com.qnium.common.client.GenericDataProvider;
import com.qnium.common.client.exceptions.DataProviderException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author admin
 */
public class ConnectionTest {
    
    public ConnectionTest() {
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
     //@Test
     public void tryeExec() throws DataProviderException {
        //GenericDataProvider.Init("http://localhost:8080/api");
        GenericDataProvider<TestData> provider = GenericDataProvider.getInstance(TestData.class);
        ReadRequestParameters req = new ReadRequestParameters();
        req.startIndex = 0;
        req.count = 0;

        //provider.executeAction("users", "read", req, CollectionResponseMessage);
        CollectionResponseMessage<TestData> read = provider.read("news", "read", 0, 0, null);
        for (TestData item: read.data)
            System.out.println(item.test);
     }
}
