package notifications.tests;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.qnium.common.notifications.NotificationManager;
import com.qnium.common.notifications.StandardNotificationTemplateProvider;
import notifications.tests.implementations.ReceipientsProvider;
import notifications.tests.implementations.TestAdapter;
import notifications.tests.notificationClasses.UserCreated;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nbv
 */
public class TestNotifications {
    
    private StandardNotificationTemplateProvider _templateProvider;
    private TestAdapter _adapter;
    private NotificationManager _nm;
    
    public TestNotifications() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        _adapter = new TestAdapter();
        
        _nm = NotificationManager.getInstance();
        
        _templateProvider = new StandardNotificationTemplateProvider();
        _templateProvider.registerNotification(UserCreated.class).addTemplate("email", "Hello {{Username}}");
        _nm.registerContactProvider(new ReceipientsProvider());
        _nm.registerTemplateProvider(_templateProvider);
        _nm.registerNotificationAdapter( _adapter );
        //assertEquals(, this);
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
    public void simpleNotification()
    {
        UserCreated user = new UserCreated();
        user.Username = "TestUser";
        _adapter.setTestHandler( ( address, message ) -> 
        { 
            assertEquals(address, "test1@test.com");
            assertEquals(message, "Hello "+user.Username);
        } );
        _nm.notifyBroadCast(user);
    }
}
