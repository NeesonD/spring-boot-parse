package com.neeson.springbootparse.env;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author neeson
 */
public class CustomKeyValue {

    private Map<String, Object> keyValue = new HashMap<>();

    public Object get(String key) {
        return keyValue.get(key);
    }

}
