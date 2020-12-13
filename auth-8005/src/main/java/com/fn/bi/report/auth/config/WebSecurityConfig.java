package com.fn.bi.report.auth.config;

import com.fn.bi.report.auth.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Resource
    private UserService userService;

    /**
     * 密码加密器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//
//        auth.inMemoryAuthentication()
//                // 在内存中创建用户并为密码加密
//                .withUser("user").password(passwordEncoder().encode("123456")).roles("USER")
//                .and()
//                .withUser("admin").password(passwordEncoder().encode("123456")).roles("ADMIN");
        auth.userDetailsService(userService);
    }

    /**
     * 安全拦截机制
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                // 放行
                .antMatchers("/auth/**").permitAll()
                .antMatchers("/actuator/**").permitAll()
                // 其他请求必须认证通过
                .anyRequest().authenticated()
                .and()
                .formLogin() // 允许表单登录
//                .successForwardUrl("/login-success") //自定义登录成功跳转页
                .and()
                .csrf().disable();
    }



    /**
     * 认证管理器配置
     */
    @Bean
    @Override
    protected AuthenticationManager authenticationManager() {
        return authentication -> daoAuthenticationProvider().authenticate(authentication);
    }

    /**
     * 认证是由 AuthenticationManager 来管理的，但是真正进行认证的是 AuthenticationManager 中定义的 AuthenticationProvider，用于调用userDetailsService进行验证
     */
    @Bean
    public AuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userService);
        daoAuthenticationProvider.setHideUserNotFoundExceptions(false);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    /**
     * 用户详情服务
     */
//    @Bean
//    @Override
//    protected UserDetailsService userDetailsService() {
//        // 测试方便采用内存存取方式
//        InMemoryUserDetailsManager userDetailsService = new InMemoryUserDetailsManager();
//        userDetailsService.createUser(User.withUsername("user1").password(passwordEncoder().encode("123456")).authorities("ROLE_USER").build());
//        userDetailsService.createUser(User.withUsername("user2").password(passwordEncoder().encode("1234567")).authorities("ROLE_USER").build());
//        return userDetailsService;
//    }

    /**
     * 设置授权码模式的授权码如何存取，暂时采用内存方式
     */
//    @Bean
//    public AuthorizationCodeServices authorizationCodeServices() {
//        return new InMemoryAuthorizationCodeServices();
//    }

    /**
     * jwt token解析器
     */
//    @Bean
//    public JwtAccessTokenConverter accessTokenConverter() {
//
//        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
//        // 对称密钥，资源服务器使用该密钥来验证
//        converter.setSigningKey("backendToken");
//        return converter;
//    }
}

