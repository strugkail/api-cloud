package com.fn.bi.report.auth.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fn.bi.backend.common.entity.Permission;
import com.fn.bi.report.auth.mapper.PermissionMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 权限表 服务实现类
 * </p>
 *
 * @author gyf
 * @since 2020-11-25
 */
@Service
public class PermissionService extends ServiceImpl<PermissionMapper, Permission> {
    @Resource
    private PermissionMapper permissionMapper;

    public List<String> getUrlByEnName(String enName) {
        return permissionMapper.getUrlByEnName(enName);
    }
}
