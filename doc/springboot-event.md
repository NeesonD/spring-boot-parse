### Springboot 的事件体系

Springboot 的事件体系是基于观察者模式，主要由三个基本组件组成，分别是ApplicationEvent、ApplicationListener、ApplicationEventMulticaster

前两者比较简单，我们主要关注 ApplicationEventMulticaster

```java
public interface ApplicationEventMulticaster {
    // 添加监听者
    void addApplicationListener(ApplicationListener<?> var1);
    // 结合 IOC 容器，通过 beanName 来添加监听者
    void addApplicationListenerBean(String var1);
    // 删除监听者
    void removeApplicationListener(ApplicationListener<?> var1);

    void removeApplicationListenerBean(String var1);

    void removeAllListeners();
    // 发射事件
    void multicastEvent(ApplicationEvent var1);

    void multicastEvent(ApplicationEvent var1, @Nullable ResolvableType var2);
}
```

这个发射器看起来功能也很简单，主要是增减监听者，并且发送事件。但是如果要用于生产，那考量的点就会多很多

我们先来回想一下开发过程中，我们是如何使用这个体系的。一般操作就是，当业务状态发生改变的时候，我们就会发射一个领域事件，然后会有多个
监听者去监听这个事件，然后处理各自的业务。这时候让我们思考几个问题

1. 事件和监听者往往是 1 对 N 的关系，spring 是如何维护这种关系的，
2. 一个项目中有那么多事件，那怎么保证监听者只监听它感兴趣的事件
3. 事件的发射是不是可以配置成异步或者同步的
4. 监听者之前是否有序，如果有序，那后面的监听者是否可以拿到前面监听者中的信息
5. 监听者是否可以并行处理
6. 推拉模型的设计

带着上面几个问题，我们来看一下 ApplicationEventMulticaster 实现类是如何处理的

首先，抽象类 AbstractApplicationEventMulticaster 提供了部分方法的实现，这表明这里面的实现是可复用的
SimpleApplicationEventMulticaster 也实现了一些方法，意味着这些方法是可以定制化的，也就是我们可以自定义一个
CustomApplicationEventMulticaster


```java
public abstract class AbstractApplicationEventMulticaster implements ApplicationEventMulticaster, BeanClassLoaderAware, BeanFactoryAware {

    // 一个大篮子，所有的监听者都会加到这个里面
    private final AbstractApplicationEventMulticaster.ListenerRetriever defaultRetriever = new AbstractApplicationEventMulticaster.ListenerRetriever(false);
    // 很多小篮子，以 Event 为 key，Listeners 为 value。起到缓存作用
    final Map<AbstractApplicationEventMulticaster.ListenerCacheKey, AbstractApplicationEventMulticaster.ListenerRetriever> retrieverCache = new ConcurrentHashMap(64);
    @Nullable
    private ClassLoader beanClassLoader;
    // 通过结合 beanFactory，可以通过 bean 的类型或者 beanName 来添加 Listener
    @Nullable
    private ConfigurableBeanFactory beanFactory;
    // 锁对象
    private Object retrievalMutex;

    public AbstractApplicationEventMulticaster() {
        this.retrievalMutex = this.defaultRetriever;
    }

    
    // 1. 这里设计了一个内部类 ListenerRetriever 来hold applicationListeners
    public void addApplicationListener(ApplicationListener<?> listener) {
        synchronized(this.retrievalMutex) {
            Object singletonTarget = AopProxyUtils.getSingletonTarget(listener);
            if (singletonTarget instanceof ApplicationListener) {
                this.defaultRetriever.applicationListeners.remove(singletonTarget);
            }
            // 从这里可以看到，所有的鸡蛋都是放在一个篮子里面的。也就是说如果我想获取某个事件的监听者，
            // 那我就要遍历所有的 applicationListeners，这样性能是很差的
            this.defaultRetriever.applicationListeners.add(listener);
            this.retrieverCache.clear();
        }
    }

    protected Collection<ApplicationListener<?>> getApplicationListeners(ApplicationEvent event, ResolvableType eventType) {
        Object source = event.getSource();
        Class<?> sourceType = source != null ? source.getClass() : null;
        AbstractApplicationEventMulticaster.ListenerCacheKey cacheKey = new AbstractApplicationEventMulticaster.ListenerCacheKey(eventType, sourceType);
        AbstractApplicationEventMulticaster.ListenerRetriever retriever = (AbstractApplicationEventMulticaster.ListenerRetriever)this.retrieverCache.get(cacheKey);
        if (retriever != null) {
            return retriever.getApplicationListeners();
        } else if (this.beanClassLoader == null || ClassUtils.isCacheSafe(event.getClass(), this.beanClassLoader) && (sourceType == null || ClassUtils.isCacheSafe(sourceType, this.beanClassLoader))) {
            synchronized(this.retrievalMutex) {
                retriever = (AbstractApplicationEventMulticaster.ListenerRetriever)this.retrieverCache.get(cacheKey);
                if (retriever != null) {
                    return retriever.getApplicationListeners();
                } else {
                    retriever = new AbstractApplicationEventMulticaster.ListenerRetriever(true);
                    Collection<ApplicationListener<?>> listeners = this.retrieveApplicationListeners(eventType, sourceType, retriever);
                    this.retrieverCache.put(cacheKey, retriever);
                    return listeners;
                }
            }
        } else {
            return this.retrieveApplicationListeners(eventType, sourceType, (AbstractApplicationEventMulticaster.ListenerRetriever)null);
        }
    }

    private Collection<ApplicationListener<?>> retrieveApplicationListeners(ResolvableType eventType, @Nullable Class<?> sourceType, @Nullable AbstractApplicationEventMulticaster.ListenerRetriever retriever) {
        List<ApplicationListener<?>> allListeners = new ArrayList();
        LinkedHashSet listeners;
        LinkedHashSet listenerBeans;
        synchronized(this.retrievalMutex) {
            listeners = new LinkedHashSet(this.defaultRetriever.applicationListeners);
            listenerBeans = new LinkedHashSet(this.defaultRetriever.applicationListenerBeans);
        }

        Iterator var7 = listeners.iterator();

        while(var7.hasNext()) {
            ApplicationListener<?> listener = (ApplicationListener)var7.next();
            if (this.supportsEvent(listener, eventType, sourceType)) {
                if (retriever != null) {
                    retriever.applicationListeners.add(listener);
                }

                allListeners.add(listener);
            }
        }

        if (!listenerBeans.isEmpty()) {
            ConfigurableBeanFactory beanFactory = this.getBeanFactory();
            Iterator var15 = listenerBeans.iterator();

            while(var15.hasNext()) {
                String listenerBeanName = (String)var15.next();

                try {
                    if (this.supportsEvent(beanFactory, listenerBeanName, eventType)) {
                        ApplicationListener<?> listener = (ApplicationListener)beanFactory.getBean(listenerBeanName, ApplicationListener.class);
                        if (!allListeners.contains(listener) && this.supportsEvent(listener, eventType, sourceType)) {
                            if (retriever != null) {
                                if (beanFactory.isSingleton(listenerBeanName)) {
                                    retriever.applicationListeners.add(listener);
                                } else {
                                    retriever.applicationListenerBeans.add(listenerBeanName);
                                }
                            }

                            allListeners.add(listener);
                        }
                    } else {
                        Object listener = beanFactory.getSingleton(listenerBeanName);
                        if (retriever != null) {
                            retriever.applicationListeners.remove(listener);
                        }

                        allListeners.remove(listener);
                    }
                } catch (NoSuchBeanDefinitionException var11) {
                }
            }
        }

        AnnotationAwareOrderComparator.sort(allListeners);
        if (retriever != null && retriever.applicationListenerBeans.isEmpty()) {
            retriever.applicationListeners.clear();
            retriever.applicationListeners.addAll(allListeners);
        }

        return allListeners;
    }

    private boolean supportsEvent(ConfigurableBeanFactory beanFactory, String listenerBeanName, ResolvableType eventType) {
        Class<?> listenerType = beanFactory.getType(listenerBeanName);
        if (listenerType != null && !GenericApplicationListener.class.isAssignableFrom(listenerType) && !SmartApplicationListener.class.isAssignableFrom(listenerType)) {
            if (!this.supportsEvent(listenerType, eventType)) {
                return false;
            } else {
                try {
                    BeanDefinition bd = beanFactory.getMergedBeanDefinition(listenerBeanName);
                    ResolvableType genericEventType = bd.getResolvableType().as(ApplicationListener.class).getGeneric(new int[0]);
                    return genericEventType == ResolvableType.NONE || genericEventType.isAssignableFrom(eventType);
                } catch (NoSuchBeanDefinitionException var7) {
                    return true;
                }
            }
        } else {
            return true;
        }
    }

    protected boolean supportsEvent(Class<?> listenerType, ResolvableType eventType) {
        ResolvableType declaredEventType = GenericApplicationListenerAdapter.resolveDeclaredEventType(listenerType);
        return declaredEventType == null || declaredEventType.isAssignableFrom(eventType);
    }

    protected boolean supportsEvent(ApplicationListener<?> listener, ResolvableType eventType, @Nullable Class<?> sourceType) {
        GenericApplicationListener smartListener = listener instanceof GenericApplicationListener ? (GenericApplicationListener)listener : new GenericApplicationListenerAdapter(listener);
        return ((GenericApplicationListener)smartListener).supportsEventType(eventType) && ((GenericApplicationListener)smartListener).supportsSourceType(sourceType);
    }

    private class ListenerRetriever {
        public final Set<ApplicationListener<?>> applicationListeners = new LinkedHashSet();
        public final Set<String> applicationListenerBeans = new LinkedHashSet();
        private final boolean preFiltered;

        public ListenerRetriever(boolean preFiltered) {
            this.preFiltered = preFiltered;
        }

        public Collection<ApplicationListener<?>> getApplicationListeners() {
            List<ApplicationListener<?>> allListeners = new ArrayList(this.applicationListeners.size() + this.applicationListenerBeans.size());
            allListeners.addAll(this.applicationListeners);
            if (!this.applicationListenerBeans.isEmpty()) {
                BeanFactory beanFactory = AbstractApplicationEventMulticaster.this.getBeanFactory();
                Iterator var3 = this.applicationListenerBeans.iterator();

                while(var3.hasNext()) {
                    String listenerBeanName = (String)var3.next();

                    try {
                        ApplicationListener<?> listener = (ApplicationListener)beanFactory.getBean(listenerBeanName, ApplicationListener.class);
                        if (this.preFiltered || !allListeners.contains(listener)) {
                            allListeners.add(listener);
                        }
                    } catch (NoSuchBeanDefinitionException var6) {
                    }
                }
            }

            if (!this.preFiltered || !this.applicationListenerBeans.isEmpty()) {
                AnnotationAwareOrderComparator.sort(allListeners);
            }

            return allListeners;
        }
    }

    private static final class ListenerCacheKey implements Comparable<AbstractApplicationEventMulticaster.ListenerCacheKey> {
        private final ResolvableType eventType;
        @Nullable
        private final Class<?> sourceType;

        public ListenerCacheKey(ResolvableType eventType, @Nullable Class<?> sourceType) {
            Assert.notNull(eventType, "Event type must not be null");
            this.eventType = eventType;
            this.sourceType = sourceType;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            } else if (!(other instanceof AbstractApplicationEventMulticaster.ListenerCacheKey)) {
                return false;
            } else {
                AbstractApplicationEventMulticaster.ListenerCacheKey otherKey = (AbstractApplicationEventMulticaster.ListenerCacheKey)other;
                return this.eventType.equals(otherKey.eventType) && ObjectUtils.nullSafeEquals(this.sourceType, otherKey.sourceType);
            }
        }

        public int hashCode() {
            return this.eventType.hashCode() * 29 + ObjectUtils.nullSafeHashCode(this.sourceType);
        }

        public String toString() {
            return "ListenerCacheKey [eventType = " + this.eventType + ", sourceType = " + this.sourceType + "]";
        }

        public int compareTo(AbstractApplicationEventMulticaster.ListenerCacheKey other) {
            int result = this.eventType.toString().compareTo(other.eventType.toString());
            if (result == 0) {
                if (this.sourceType == null) {
                    return other.sourceType == null ? 0 : -1;
                }

                if (other.sourceType == null) {
                    return 1;
                }

                result = this.sourceType.getName().compareTo(other.sourceType.getName());
            }

            return result;
        }
    }
}

```

