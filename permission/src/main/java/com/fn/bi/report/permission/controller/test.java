package com.fn.bi.report.permission.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@RestController
public class test {
    @Resource
    WebApplicationContext webApplicationContext;

    @GetMapping("/test")
    public void test2() {
        Set<String> result = new TreeSet<>();
        RequestMappingHandlerMapping bean = webApplicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = bean.getHandlerMethods();
        for (RequestMappingInfo rmi : handlerMethods.keySet()) {
            PatternsRequestCondition pc = rmi.getPatternsCondition();
            Set<String> pSet = pc.getPatterns();
            pSet.forEach(url -> {
                if (result.contains(url)) {
                    System.out.println(url);
                    throw new RuntimeException("url重复");
                }
            });
            result.addAll(pSet);
        }
        result.forEach(System.out::println);
    }
}
