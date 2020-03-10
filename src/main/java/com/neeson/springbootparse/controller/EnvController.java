package com.neeson.springbootparse.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.neeson.springbootparse.constant.EnvConstant.PORT;

@RestController
@RequestMapping("/EnvController")
public class EnvController {

    @Autowired
    private ApplicationContext applicationContext;

    @GetMapping("/getPort")
    public Object getPort() {
        return applicationContext.getEnvironment().getProperty(PORT);
    }

    @GetMapping("/getName")
    public Object getName() {
        return applicationContext.getEnvironment().getProperty("name");
    }

    @GetMapping("/getAge")
    public Object getAge() {
        return applicationContext.getEnvironment().getProperty("age");
    }

}
