/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.notifications.interfaces;

import com.qnium.common.notifications.data.NotificationContact;
import java.util.List;

/**
 *
 * @author nbv
 */
public interface INotificationContactsProvider {
    
    public List<NotificationContact> getNotificationRecepients(Object notificationClass);
}
