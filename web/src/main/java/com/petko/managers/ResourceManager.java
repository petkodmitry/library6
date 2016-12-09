package com.petko.managers;

import org.springframework.stereotype.Component;

import java.util.ResourceBundle;

@Component
public class ResourceManager {
    private final ResourceBundle bundle = ResourceBundle.getBundle("config");

    public String getProperty(String key){
        return bundle.getString(key);
    }
}
