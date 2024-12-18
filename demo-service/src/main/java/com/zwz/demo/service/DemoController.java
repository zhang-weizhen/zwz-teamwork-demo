package com.zwz.demo.service;

import com.zwz.demo.fegin.Demo1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/demo")
public class DemoController {
    @Autowired
    ApplicationContext applicationContext;

    @GetMapping("/testApi")
    public String testApi(){
        Demo1Service demo1Service = (Demo1Service)applicationContext.getBean(Demo1Service.class.getName());
        System.out.println(demo1Service.testApi1());
        return "testApi";
    }


}
