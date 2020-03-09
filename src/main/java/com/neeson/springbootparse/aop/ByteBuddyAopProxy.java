package com.neeson.springbootparse.aop;

import org.springframework.aop.framework.AopProxy;

public class ByteBuddyAopProxy implements AopProxy {
    @Override
    public Object getProxy() {
        return null;
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return null;
    }
}
