package com.neeson.springbootparse.resource;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public class CustomResourceLoader implements ResourceLoader {
    @Override
    public Resource getResource(String s) {
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        return null;
    }
}
