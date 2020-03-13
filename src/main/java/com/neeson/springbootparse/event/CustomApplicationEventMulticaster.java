package com.neeson.springbootparse.event;

import com.neeson.springbootparse.event.anno.KafkaMqEvent;
import com.neeson.springbootparse.event.anno.RabbitMqEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;


public class CustomApplicationEventMulticaster extends SimpleApplicationEventMulticaster {

    BlockingQueue blockingQueue = new LinkedBlockingQueue();

    @Override
    public void multicastEvent(final ApplicationEvent event, @Nullable ResolvableType eventType) {
        // 我们可以通过 event 的类型，来选择事件的发送方式
        if (event instanceof KafkaMqEvent) {

        } else if (event instanceof RabbitMqEvent) {

        } else {
            // 这里的 ResolvableType 可以简单看作是一个 event 的唯一key，用来获取对应的 Listener
            ResolvableType type = (eventType != null ? eventType : resolveDefaultEventType(event));
            Executor executor = getTaskExecutor();
            // 这里会获取 event 对应的 listener；@EventListener 是如何处理的
            for (ApplicationListener<?> listener : getApplicationListeners(event, type)) {
                // 可以看到，消费者可以以异步的方式去执行; 也就是说这里存在同步异步策略
                // 并且是推模型
                // 思考一下如何改成拉模型，或者推拉模型结合（对比一下这三种模型的优劣）
                if (executor != null) {
                    executor.execute(() -> invokeListener(listener, event));
                }
                else {
                    invokeListener(listener, event);
                }
            }
        }
    }

    private ResolvableType resolveDefaultEventType(ApplicationEvent event) {
        return ResolvableType.forInstance(event);
    }

}
