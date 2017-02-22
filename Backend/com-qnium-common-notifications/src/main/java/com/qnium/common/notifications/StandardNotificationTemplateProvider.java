/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.notifications;

import com.qnium.common.notifications.interfaces.INotificationTemplateProvider;
import java.util.ArrayList;
import java.util.Optional;

/**
 *
 * @author nbv
 */
public class StandardNotificationTemplateProvider implements INotificationTemplateProvider {

    public StandardNotificationTemplateProvider() {
        this._notificationConfigs = new ArrayList<>();
    }
    
    private ArrayList<NotificationConfiguration> _notificationConfigs;

    //Gets template for defined notification by selected channel, returns null if no template found
    @Override
    public String getTemplateByChannel(Object notification, String channelName) {
        Optional<NotificationConfiguration> found = _notificationConfigs.stream().filter( n -> n._notificationClass == notification.getClass() && n.getTemplateByChannel(channelName)!=null ).findAny();
        if (found.isPresent())
        {
            return found.get().getTemplateByChannel(channelName);
        }
        
        return null;
    }
    
    public NotificationConfiguration registerNotification(Class notificationClass)
    {
        NotificationConfiguration notificationConfiguration = new NotificationConfiguration(notificationClass);
        _notificationConfigs.add(notificationConfiguration);
        return notificationConfiguration;
    }
}
