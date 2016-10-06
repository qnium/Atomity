/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.notifications;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

/**
 *
 * @author Kirill Zhukov
 */
public class NotificationConfiguration {

    private class ChannelTemplate{
        String channel;
        String template;
    }
    
    Class _notificationClass;
    
    ArrayList<ChannelTemplate> _templates;
    
    public NotificationConfiguration(Class notificationClass)
    {
        _notificationClass = notificationClass;
        _templates = new ArrayList<>();
    }
    
    public String getTemplateByChannel(String channel)
    {
        Optional<ChannelTemplate> found = _templates.stream().filter( t -> t.channel.equals(channel)).findAny();
        if (found.isPresent())
            return found.get().template;
        
        return null;
    }
    
    public NotificationConfiguration addTemplate(String channel, String template)
    {
        
        Optional<ChannelTemplate> found = _templates.stream().filter( t -> t.channel.equals(channel)).findAny();
        if (found.isPresent())
        {
            found.get().template = template;
        } else
        {
            ChannelTemplate tpl = new ChannelTemplate();
            tpl.channel = channel;
            tpl.template = template;
                    
            _templates.add(tpl);
        }
        
        return this;
    }
}
