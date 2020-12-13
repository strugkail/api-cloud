package com.fn.bi.report.permission.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fn.bi.report.permission.mapper.RoleMapper;
import com.fn.bi.backend.common.entity.Role;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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
    @Resource
    private RoleMapper roleMapper;

    public List<String> getRoleByEnName(String enName) {
        return roleMapper.getRoleByEnName(enName);
    }

    public List<Role> adminList() {
        return roleMapper.selectList(null);
    }

    public List<Role> getRoleByUserId(Integer userId){
        return roleMapper.getRoleByUserId(userId);
    }
}
