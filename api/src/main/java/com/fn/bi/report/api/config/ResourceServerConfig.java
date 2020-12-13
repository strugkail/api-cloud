package com.fn.bi.report.api.config;

import com.fn.bi.report.api.entity.Permission;
import com.fn.bi.report.api.mapper.PermissionMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

import javax.annotation.Resource;
import java.util.List;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    @Resource
    private PermissionMapper permissionMapper;

    /**
     * 路由安全认证配置
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry urlRegistry = http.authorizeRequests();
        List<Permission> permissionList = permissionMapper.getPermission();
        for (Permission permission : permissionList) {
            urlRegistry.antMatchers(permission.getUrl()).hasAnyAuthority(permission.getEn_name());
        }
//        urlRegistry.antMatchers("/users").hasAnyAuthority("System");
        urlRegistry.and().csrf().disable();
    }


    /**
     * token服务配置
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
//        resources.tokenServices(tokenServices());
//    }
        resources.tokenServices(tokenServices());
    }
    /**
     * 资源服务令牌解析服务
     */
    @Bean
    @Primary
    public ResourceServerTokenServices tokenServices() {
        RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
        remoteTokenServices.setCheckTokenEndpointUrl("http://localhost:8081/oauth/check_token");
        remoteTokenServices.setClientId("gateway");
        remoteTokenServices.setClientSecret("gatWay");
        return remoteTokenServices;
    }
}


