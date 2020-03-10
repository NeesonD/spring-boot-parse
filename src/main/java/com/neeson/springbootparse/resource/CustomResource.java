package com.neeson.springbootparse.resource;

import org.springframework.core.io.AbstractResource;

import java.io.IOException;
import java.io.InputStream;

public class CustomResource extends AbstractResource {

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return null;
    }
}
