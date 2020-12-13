package com.fn.bi.report.auth;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
public class TestMain {
    @Test
    public void test(){
        System.out.println(new BCryptPasswordEncoder().encode("123456"));
    }
}
