package com.fn.bi.report.auth.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fn.bi.backend.common.entity.Role;
import com.fn.bi.report.auth.mapper.RoleMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author gyf
 * @since 2020-11-25
 */
@Service
public class RoleService extends ServiceImpl<RoleMapper, Role> {

}
