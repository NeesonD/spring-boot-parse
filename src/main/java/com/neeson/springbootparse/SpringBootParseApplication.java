package com.neeson.springbootparse;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class SpringBootParseApplication {

    public static void main(String[] args) {

        new SpringApplicationBuilder(SpringBootParseApplication.class)
//                .beanNameGenerator(new CustomBeanNameGenerator())
                .run(args)
        ;
    }

}
