/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.notifications.interfaces;

/**
 *
 * @author nbv
 */
public interface INotificationTemplateProvider {
    public String getTemplateByChannel(Object notification, String channelName);
}
