package com.zwz.demo.fegin;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "zwz-demo1-service")
public interface Demo1Service {

    @RequestMapping("/testApi1")
    String testApi1();
}
