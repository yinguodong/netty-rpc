package com.ygd.rpcclient.controller;

import com.ygd.rpc.common.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {
    @Autowired
    private HelloService helloService;
    @GetMapping("/sayHello")
    public String hello(String name){
        return helloService.sayHello(name);
    }
}
