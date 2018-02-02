/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sample;

import com.fasterxml.jackson.core.type.TypeReference;
import com.qnium.common.backend.assets.dataobjects.CollectionResponseMessage;
import com.qnium.common.backend.assets.dataobjects.ObjectResponseMessage;
import com.qnium.common.backend.assets.dataobjects.ResponseMessage;
import com.qnium.common.client.DataProvider;
import earchive.datamodel.Letter;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author user
 */
public class DataProviderTestTest extends TestCase {
    /**
     * Test of equal method, of class Vectors.
     */
    public void testActionExecute() throws Exception {
        DataProvider dp = new DataProvider("asd","http://localhost:8080/api/",-1);
        
        
        Letter letter = new Letter();        
        
//        CollectionResponseMessage<Letter> a = dp.executeAction("letter", "read", letter, new TypeReference<CollectionResponseMessage<Letter>>() {});
//        
//        assertEquals(a.errorCode, 0);
//        assertEquals(a.error, null);
    }

   
}
    
