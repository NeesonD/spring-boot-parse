package com.neeson.springbootparse;

import com.neeson.springbootparse.context.CustomApplicationContextInitializer;
import com.neeson.springbootparse.listener.CustomApplicationListener;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class SpringBootParseApplication {

    public static void main(String[] args) {

        new SpringApplicationBuilder(SpringBootParseApplication.class)
//                .beanNameGenerator(new CustomBeanNameGenerator())
                .listeners(new CustomApplicationListener())
                .initializers(new CustomApplicationContextInitializer())
                .run(args)
        ;
    }

}
