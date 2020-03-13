package com.neeson.springbootparse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.GenericWebApplicationContext;

@SpringBootTest
class SpringBootParseApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        GenericWebApplicationContext configurableApplicationContext = (GenericWebApplicationContext) applicationContext;
        Resource resource = applicationContext.getResource("/application.yaml");

    }

}
