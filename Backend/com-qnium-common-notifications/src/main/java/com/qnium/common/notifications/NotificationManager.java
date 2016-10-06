/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.notifications;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.qnium.common.notifications.data.NotificationContact;
import com.qnium.common.notifications.interfaces.INotificationAdapter;
import com.qnium.common.notifications.interfaces.INotificationTemplateProvider;
import java.util.ArrayList;
import com.qnium.common.notifications.interfaces.INotificationContactsProvider;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author nbv
 */
public class NotificationManager {
    
    private INotificationTemplateProvider _templateProvider;
    private INotificationContactsProvider _contactProvider;
    private ArrayList<INotificationAdapter> _adapters;
    
    private NotificationManager() {
        _templateProvider = new INotificationTemplateProvider() {
            @Override
            public String getTemplateByChannel(Class notificationClass, String channelName) {
                return null;
            }
        };
        
        _contactProvider = ( notification ) -> { return new ArrayList<NotificationContact>(); };
        
        _adapters = new ArrayList<>();
    }
    
    public static NotificationManager getInstance() {
        return NotificationManagerHolder.INSTANCE;
    }
    
    public void registerTemplateProvider(INotificationTemplateProvider provider)
    {
        _templateProvider = provider;
    }
    
    public void registerNotificationAdapter(INotificationAdapter adapter)
    {
        _adapters.add(adapter);
    }
    
    public void registerContactProvider(INotificationContactsProvider provider)
    {
        _contactProvider = provider;
    }
    
    private static class NotificationManagerHolder {
        private static final NotificationManager INSTANCE = new NotificationManager();
    }
    
    public void notifyBroadCast(Object notification)
    {
       notifyGroup(notification, false);
    }
    
    private String fillTemplate(Object object, String template)
    {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(new StringReader(template), "template.hash");
        StringWriter writer = new StringWriter();
        return mustache.execute(writer, object).toString();
    }
    
    public void notifyEveryBody(Object notification)
    {
        notifyGroup(notification, true);
    }
    
    private void notifyGroup(Object notification, boolean perContactData)
    {
        for( INotificationAdapter adapter : _adapters )
        {
            String channel = adapter.getChannel();
            String template = _templateProvider.getTemplateByChannel(notification.getClass(), channel);
            
            NotificationContact [] contacts = (NotificationContact[]) _contactProvider.getNotificationRecepients(notification).stream().filter( n -> n.channel.equals(channel)).toArray(NotificationContact[]::new);
            
            String message = fillTemplate(notification, template);
            
            if (!perContactData)
            {
                adapter.sendNotification(contacts, message);
            }
            else{
                for ( NotificationContact contact: contacts)
                {
                    message = fillTemplate(contact, message);
                    NotificationContact[] contactArray = new NotificationContact[1];
                    contactArray[0] = contact;
                    adapter.sendNotification(contactArray, message);
                }
            }
        }
    }
    
    public void notifyPersonally(Object notification, NotificationContact contact)
    {
        String channel = contact.channel;
        
        List<INotificationAdapter> adapters = _adapters.stream()
                .filter( a -> a.getChannel().equals(channel))
                .collect(Collectors.toList());
        
        String template = _templateProvider.getTemplateByChannel(notification.getClass(), channel);
        String message = fillTemplate(notification, template);
        message = fillTemplate(contact, message);
        
        for (INotificationAdapter adapter: adapters)
        {
            NotificationContact[] contactArray = new NotificationContact[1];
            contactArray[0] = contact;
            adapter.sendNotification(contactArray, message);
        }
        
    }
}
