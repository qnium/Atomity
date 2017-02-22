/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.common.notifications;

import com.qnium.common.notifications.interfaces.INotificationTemplateProvider;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Ivan
 */
public class FileTemplateProvider implements INotificationTemplateProvider {
        
    private final List<NotificationConfiguration> notificationConfigs =
            new ArrayList<>();

    @Override
    public String getTemplateByChannel(Class notificationClass, String channelName) {
        Optional<NotificationConfiguration> found = notificationConfigs.stream()
                .filter(n -> n._notificationClass == notificationClass 
                        && n.getTemplateByChannel(channelName)!=null)
                .findAny();
        
        if (found.isPresent()) {
            String resourcePath = found.get().getTemplateByChannel(channelName);
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            try {
                InputStream is = cl.getResourceAsStream(resourcePath);
                return IOUtils.toString(is, StandardCharsets.UTF_8.name());
            }
            catch (Exception ex) {
                return null;
            }
        }
        
        return null;
    }
    
    /**
     * Registers new notification. Templates should be added as paths to 
     * resource files.
     * @param notificationClass
     * @return 
     */
    public NotificationConfiguration registerNotification(Class notificationClass) {
        NotificationConfiguration notificationConfiguration = new NotificationConfiguration(notificationClass);
        notificationConfigs.add(notificationConfiguration);
        return notificationConfiguration;
    } 
}
