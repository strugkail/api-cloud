package com.generator.service.impl;

import com.generator.entity.User;
import com.generator.mapper.UserMapper;
import com.generator.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author gyf
 * @since 2020-12-07
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
