package com.neeson.springbootparse.listener;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AnnoEventListener {

    @EventListener
    public void listen(ApplicationEvent event) {

    }

}
