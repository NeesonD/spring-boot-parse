package com.neeson.springbootparse.env;

import org.springframework.core.env.PropertySource;

/**
 * @author neeson
 */
public class CustomPropertySource extends PropertySource<CustomKeyValue> {

    private static final String CUSTOM_PROPERTY_SOURCE = "CUSTOM_PROPERTY_SOURCE";

    public CustomPropertySource(CustomKeyValue source) {
        super(CUSTOM_PROPERTY_SOURCE, source);
    }

    @Override
    public Object getProperty(String key) {
        return source.get(key);
    }
}
