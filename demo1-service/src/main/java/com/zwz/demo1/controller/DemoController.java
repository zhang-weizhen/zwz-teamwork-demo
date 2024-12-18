package com.zwz.demo1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/demo")
public class DemoController {


    @GetMapping("/testApi1")
    public String testApi(){
        return "testApi1";
    }


}
