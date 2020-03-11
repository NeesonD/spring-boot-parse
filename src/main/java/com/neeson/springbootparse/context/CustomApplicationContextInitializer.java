package com.neeson.springbootparse.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import static com.neeson.springbootparse.constant.EnvConstant.LOG_PRE;

@Slf4j
public class CustomApplicationContextInitializer implements ApplicationContextInitializer {
    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
       log.error(LOG_PRE + this.getClass().getSimpleName() + configurableApplicationContext.getClass().getSimpleName());
    }
}
