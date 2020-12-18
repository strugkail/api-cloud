package com.fn.bi.report.auth.config;

import com.fn.bi.report.auth.service.TokenService;
import com.fn.bi.report.auth.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    @Resource
    private UserService userService;
    @Resource
    private DataSource dataSource;
    /**
     * 认证管理器
     */
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private RedisConnectionFactory redisConnectionFactory;

//    @Resource
//    private DefaultTokenServices defaultTokenServices;

    /**
     * 客户端详情服务
     */
//    private final ClientDetailsService clientDetailsService;

    /**
     * 授权码服务
     */
//    private final AuthorizationCodeServices authorizationCodeServices;
    /**
     * jwtToken解析器
     */
//    private final JwtAccessTokenConverter jwtAccessTokenConverter;


    /**
     * jwt token 校验解析器
     */
//    @Bean
//    public TokenStore tokenStore() {
//        return new JdbcTokenStore(dataSource);
//    }
    @Bean
    public TokenStore tokenStore() {
        return new RedisTokenStore(redisConnectionFactory);
    }

//    @Bean
//    public DefaultTokenServices tokenService() {
//        DefaultTokenServices tokenServices = new DefaultTokenServices();
//        //配置token存储
//        tokenServices.setTokenStore(tokenStore());
//        //开启支持refresh_token，此处如果之前没有配置，启动服务后再配置重启服务，可能会导致不返回token的问题，解决方式：清除redis对应token存储
//        tokenServices.setSupportRefreshToken(true);
//        //复用refresh_token
//        tokenServices.setReuseRefreshToken(false);
//        //token有效期，设置12小时
//        tokenServices.setAccessTokenValiditySeconds(12 * 60 * 60);
//        //refresh_token有效期，设置一周
//        tokenServices.setRefreshTokenValiditySeconds(7 * 24 * 60 * 60);
//        return tokenServices;
//    }

    @Bean
    public ClientDetailsService jdbcClientDetails() {
        return new JdbcClientDetailsService(dataSource);
    }

    /**
     * 客户端详情服务配置 （demo采用本地内存存储）
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(jdbcClientDetails());
//        clients
//                // 使用本地内存存储
//                .inMemory()
//                // 客户端id
//                .withClient("client_1")
//                // 客户端密码
//                .secret(new BCryptPasswordEncoder().encode("123456"))
//                // 该客户端允许授权的类型
//                .authorizedGrantTypes("authorization_code", "password", "client_credentials", "implicit", "refresh_token")
//                // 该客户端允许授权的范围
//                .scopes("all")
//                // false跳转到授权页面，true不跳转，直接发令牌
//                .autoApprove(true);
//        clients
//                // 使用内存设置
//                .inMemory()
//                // client_id
//                .withClient("client")
//                // client_secret
//                .secret(passwordEncoder.encode("client"))
//                // 授权类型
//                .authorizedGrantTypes("authorization_code", "password", "client_credentials", "implicit", "refresh_token")
//                // 授权范围
//                .scopes("app")
//                // 注册回调地址
//                .redirectUris("http://www.funtl.com")
//                .autoApprove(false);
//        new JdbcClientDetailsService(dataSource);
    }

    /**
     * 配置访问令牌端点
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
//        endpoints
//
//                // 授权码服务
//                .authorizationCodeServices(authorizationCodeServices)
//                // 令牌管理服务
//                .tokenServices(tokenServices())
//                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
        endpoints.tokenStore(tokenStore())
                // 认证管理器
                .authenticationManager(authenticationManager)
                .userDetailsService(userService);
//        .tokenServices(defaultTokenServices);
    }

    /**
     * 配置令牌端点安全约束
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security
                // oauth/check_token公开
                .checkTokenAccess("permitAll()")
                // oauth/token_key 公开密钥
                .tokenKeyAccess("permitAll()")
                // 允许表单认证
                .allowFormAuthenticationForClients();
    }

    /**
     * token持久化配置
     */
//    @Bean
//    public TokenStore tokenStore() {
//        // 本地内存存储令牌
//        return new JdbcTokenStore(dataSource);
////        return null;
//    }


    /**
     * Token转换器必须与认证服务一致
     */
//    @Bean
//    public JwtAccessTokenConverter accessTokenConverter() {
//        JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
//        accessTokenConverter.setSigningKey("backendToken");
//        return accessTokenConverter;
//    }

    /**
     * 令牌服务配置
     *
     * @return 令牌服务对象
     */
//    public AuthorizationServerTokenServices tokenServices() {
//        DefaultTokenServices tokenServices = new DefaultTokenServices();
//        tokenServices.setTokenStore(tokenStore());
//        tokenServices.setSupportRefreshToken(true);
////     令牌增强
//        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
//        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(jwtAccessTokenConverter));
//        tokenServices.setTokenEnhancer(tokenEnhancerChain);
//        // 令牌默认有效期2小时
//        tokenServices.setAccessTokenValiditySeconds(60);
//        // 刷新令牌默认有效期3天
//        tokenServices.setRefreshTokenValiditySeconds(259200);
//        return tokenServices;
//    }

}

