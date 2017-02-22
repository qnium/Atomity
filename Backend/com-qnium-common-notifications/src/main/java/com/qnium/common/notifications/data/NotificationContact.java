/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.notifications.data;

/**
 *
 * @author nbv
 */
public class NotificationContact {

    public NotificationContact(String channel, String contactID) {
        this.channel = channel;
        this.contactID = contactID;
    }
    
    public String channel;
    public String contactID;
    public String name;
    
}
