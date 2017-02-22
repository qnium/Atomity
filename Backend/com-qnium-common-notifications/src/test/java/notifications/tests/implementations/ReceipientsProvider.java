/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notifications.tests.implementations;

import com.qnium.common.notifications.data.NotificationContact;
import java.util.ArrayList;
import java.util.List;
import notifications.tests.notificationClasses.UserCreated;
import com.qnium.common.notifications.interfaces.INotificationContactsProvider;

/**
 *
 * @author nbv
 */
public class ReceipientsProvider implements INotificationContactsProvider {

    @Override
    public List<NotificationContact> getNotificationRecepients(Object notificationClass) {
        ArrayList<NotificationContact> contactList = new ArrayList();
        
        if (notificationClass.getClass() == UserCreated.class)
        {
            contactList.add(new NotificationContact( "email", "test1@test.com"));
            contactList.add(new NotificationContact( "sms", "+111"));
        }
        
        return contactList;
    }
    
}
