package com.neeson.springbootparse.env;

import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @see  ConfigFileApplicationListener
 */
public class FuckPropertySourceLoader implements PropertySourceLoader {

    private static final String XML_FILE_EXTENSION = ".fuck";

    @Override
    public String[] getFileExtensions() {
        return new String[] { "fuck" };
    }

    @Override
    public List<PropertySource<?>> load(String name, Resource resource) throws IOException {
        Map<String, ?> properties = loadProperties(resource);
        if (properties.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections
                .singletonList(new OriginTrackedMapPropertySource(name, Collections.unmodifiableMap(properties), true));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Map<String, ?> loadProperties(Resource resource) throws IOException {
        String filename = resource.getFilename();
        if (filename != null && filename.endsWith(XML_FILE_EXTENSION)) {
            return (Map) PropertiesLoaderUtils.loadProperties(resource);
        }
        return new OriginTrackedPropertiesLoader(resource).load();
    }
}
