package com.neeson.springbootparse.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;

import static com.neeson.springbootparse.constant.EnvConstant.LOG_PRE;

@Slf4j
public class FactoryApplicationListener implements ApplicationListener, Ordered {
    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        log.error(LOG_PRE + this.getClass().getSimpleName() + applicationEvent.getClass().getName());
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
