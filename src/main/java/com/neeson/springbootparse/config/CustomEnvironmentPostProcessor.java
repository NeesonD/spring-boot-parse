package com.neeson.springbootparse.config;

import com.neeson.springbootparse.env.CustomKeyValue;
import com.neeson.springbootparse.env.CustomPropertySource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.HashMap;

import static com.neeson.springbootparse.constant.EnvConstant.PORT;
import static com.neeson.springbootparse.constant.EnvConstant.PORT_NO;

/**
 * @author neeson
 */
public class CustomEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    public static final int DEFAULT_ORDER = Ordered.HIGHEST_PRECEDENCE + 6;

    private int order = DEFAULT_ORDER;

    /**
     * 这里可以从远程获取 customProperties
     * @param environment
     * @param application
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        HashMap<String, Object> customProperties = new HashMap<>();
        customProperties.put(PORT, PORT_NO);
        environment.getPropertySources().addFirst(new CustomPropertySource(new CustomKeyValue(customProperties)));
    }

    @Override
    public int getOrder() {
        return order;
    }
}
