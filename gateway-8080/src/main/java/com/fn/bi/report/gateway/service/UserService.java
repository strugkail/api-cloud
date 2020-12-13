package com.fn.bi.report.gateway.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fn.bi.report.gateway.dto.Admin;
import com.fn.bi.report.gateway.mapper.UserMapper;
import com.fn.bi.backend.common.entity.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
public class UserService extends ServiceImpl<UserMapper, User> {
    @Resource
    private UserMapper userMapper;

    public List<Admin> adminList(){
        return userMapper.adminList();
    }

}
