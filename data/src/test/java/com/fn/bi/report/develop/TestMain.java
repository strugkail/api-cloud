package com.fn.bi.report.develop;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;

@SpringBootTest
public class TestMain {

    @Test
    public void test(){
        System.out.println("88888888888888888888888888888888");
    }

    @Resource
    private WebApplicationContext webApplicationContext;

//    @Test
//    public void test2() {
//        Set<String> result = new TreeSet<>();
//        RequestMappingHandlerMapping bean = webApplicationContext.getBean(RequestMappingHandlerMapping.class);
//        Map<RequestMappingInfo, HandlerMethod> handlerMethods = bean.getHandlerMethods();
//        for (RequestMappingInfo rmi : handlerMethods.keySet()) {
//            PatternsRequestCondition pc = rmi.getPatternsCondition();
//            Set<String> pSet = pc.getPatterns();
//            pSet.forEach(url -> {
//                if (result.contains(url)) {
//                    System.out.println(url);
//                    throw new RuntimeException("url重复");
//                }
//            });
//            result.addAll(pSet);
//        }
//        result.forEach(url -> {
//            System.out.println(url);
//        });
//    }

}
