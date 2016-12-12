package com.petko.managers;

import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.ResourceBundle;

@Component
public class ResourceManager {
    private final ResourceBundle bundle = ResourceBundle.getBundle("config");

    public String getProperty(String key){
        return bundle.getString(key);
    }

    public ResourceBundle getResourceBundleLocale(Locale locale) {
        return ResourceBundle.getBundle("i18n/messages", locale);
    }
}
