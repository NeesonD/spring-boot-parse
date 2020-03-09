package com.neeson.springbootparse.aop;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.AopProxyFactory;

/**
 * @author neeson
 */
public class ByteBuddyAopProxyFactory implements AopProxyFactory {
    @Override
    public AopProxy createAopProxy(AdvisedSupport advisedSupport) throws AopConfigException {
        return new ByteBuddyAopProxy();
    }
}
