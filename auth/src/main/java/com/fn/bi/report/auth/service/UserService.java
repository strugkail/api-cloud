package com.fn.bi.report.auth.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fn.bi.backend.common.entity.User;
import com.fn.bi.report.auth.mapper.PermissionMapper;
import com.fn.bi.report.auth.mapper.UserMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author gyf
 * @since 2020-11-25
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> implements UserDetailsService {

    @Resource
    private PermissionMapper backendPermissionMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getOne(Wrappers.<User>lambdaQuery().eq(User::getEnName, username));
        ArrayList<GrantedAuthority> roles = new ArrayList<>();
        if (user != null) {
            List<String> permission = backendPermissionMapper.getUrlByEnName(user.getEnName());
            for (String backendPermission : permission) {
                roles.add(new SimpleGrantedAuthority(backendPermission));
            }
        }
        assert user != null;
        return new org.springframework.security.core.userdetails.User(user.getEnName(), user.getPassword(), roles);
    }

}
