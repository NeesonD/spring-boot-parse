### 开篇

从事开发这几年，也看过几次 springboot 的源码，上一次坚持了一个多月，主要是把里面的流程给摸了一遍。因为前段时间学了 go(go 也有 web 框架)，以及重温了
一遍设计模式。

为了更好的理解其它 web 框架的设计以及设计模式的实战运用，打算再一次分析 springboot 的源码

虽然说网上已经有很多解析 springboot 源码的文章，但是知易行难。自己不亲自给源码注入一波灵魂，总感觉差点什么

阅读源码是非常有难度的，很容易让人放弃。个人认为主要是以下两个原因

* 一是代码多，不知道从哪看起，也不知道看哪些
* 二是代码层级多，实现细节复杂，看代码的时候常常陷入细节，搞一会儿就把自己给转晕了

我自己看源码也看了好几遍，前几次也是因为上面的原因，都没有坚持下来。后面因为工作上的需要，一遍一遍的去看源码，并不断地去理解源码中
的设计，这才让源码的阅读变得越来越轻松。

为了看源码的时候少踩点坑，提供几个方法：

* 读源代码一定要有目的。只有确定了目标，你才知道自己要看哪些模块
* 刚开始读源代码的时候要有导读，也就是在网上找那种一系列的源码解读文章，不要自己生啃
* 要学会在源代码上面做笔记，要动手 debug，可能的话自己写一些自定义的功能，要不然看完别人文章，没过多久就忘了
* 看完某个系列之后，要形成自己的理解，并且动手实践（这也是我现在要做的）

因为 springboot + spring 的源码非常多，所以暂时打算只分析启动的主流程，也就是 SpringApplication.run 这里面的内容

从这个系列中，我主要想介绍以下几点：

* SpringApplication.run 中的流程与细节
* springboot 是如何提供扩展功能的，以及如何利用这些扩展点来完善业务（比如说如何自己写一个简单的远程配置中心）
* springboot 中优秀的设计模式，以及如何将这些设计模式运用到日常开发中


### SpringApplication 的理解

SpringApplication 是项目启动的入口，所以从这个类开始分析。这个类是个组合类，非常的复杂。开发中对于复杂对象的构建，往往用 builder
模式，正好 springboot 也提供了 SpringApplicationBuilder 去构建这个组合对象

平常开发的时候，我们构建 SpringApplication，里面的组合对象都是默认的。但是只要是组合对象，并且有暴露 set 方法，就可以将默认组件
替换成自定义组件。比如说用 CustomBeanNameGenerator 代替 DefaultBeanNameGenerator，那就可以在不同的包里面使用相同类名的 bean 了。

这里可以引出第一个扩展点设计：**接口 + 组合 + builder模式**

一个复杂对象的初始化，往往包含必须的组件和非必须的组件，非必须的组件通过 set 来设置，必须的组件则在构造器里面初始化

![SpringApplication](./springApplication.png)

从构造器中主要关注里面的扩展点，通过自定义 spring.factories，我们可以加入自己想要的 Listener 和 Initializer，当然
也可以通过 SpringApplicationBuilder 设置。除了关注扩展点之外，我们可以看到这里有可以复用的方法 getSpringFactoriesInstances
这个方法主要通过反射来完成的，平时开发中也可以借鉴这种写法

SpringApplication 构造完成之后，通过 run 方法就可以启动项目。我们先来看一下这个 run 方法中的几个关键点

![SpringApplication.run](./springboot-core.png)

这里面主要分成三个体系：事件体系、env 体系、context 体系

**事件体系的重点**：给开发者提供强大的扩展功能

* 观察者模式
* 扩展点设计2：流程 + 事件 + 组合
* @EventListener 和 @TransactionalEventListener(ThreadLocal的运用)以及实现一个自定义的 @CustomEventListener 


**env体系重点**：保存各种 key-value 对，可以接入配置中心

* Resource 和 ResourceLoader
* PropertySource 和 PropertyResolver 以及 Profile

**context体系**：Spring 的核心

* IOC 容器
* AOP 框架
* BeanFactoryPostProcessor 的扩展能力
* BeanPostProcessor 的扩展能力








