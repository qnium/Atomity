/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notifications.tests.implementations;

import com.qnium.common.notifications.data.NotificationContact;
import com.qnium.common.notifications.interfaces.INotificationAdapter;
import java.util.List;
import notifications.tests.IHandlerInterceptor;

/**
 *
 * @author nbv
 */
public class TestAdapter implements INotificationAdapter {

    public TestAdapter() {
        this._handler = new IHandlerInterceptor() {
            @Override
            public void handle(String address, String message) {
            }
            
        };

    }

    IHandlerInterceptor _handler;
            
    @Override
    public void sendNotification(NotificationContact[] contactList, String notificationText, Object notification) {
        for(NotificationContact contact: contactList)
            _handler.handle(contact.contactID, notificationText);
    }
    
    public void setTestHandler(IHandlerInterceptor handler)
    {
        _handler = handler;
    }

    @Override
    public String getChannel() {
        return "email";
    }
    
}
