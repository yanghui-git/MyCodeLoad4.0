package com.test.other.demo.contoller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/test/demo")
public class TestDemoController {

    @GetMapping("/testOne")
    public void testOne(){

    }
}
