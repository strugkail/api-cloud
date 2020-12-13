package com.fn.bi.report.login.service;

import com.fn.bi.report.login.entity.BackendPermission;
import com.fn.bi.report.login.entity.BackendPermissionExample;
import com.fn.bi.report.login.mapper.generator.BackendPermissionMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BackendPermissionService {
    @Resource
    private BackendPermissionMapper permissionMapper;

    public Set<String> queryByRoleIds(Integer[] roleIds) {
        Set<String> permissions = new HashSet<String>();
        if(roleIds.length == 0){
            return permissions;
        }

        BackendPermissionExample example = new BackendPermissionExample();
        example.or().andRoleIdIn(Arrays.asList(roleIds)).andDeletedEqualTo(false);
        List<BackendPermission> permissionList = permissionMapper.selectByExample(example);

        for(BackendPermission permission : permissionList){
            permissions.add(permission.getPermission());
        }

        return permissions;
    }


    public Set<String> queryByRoleId(Integer roleId) {
        Set<String> permissions = new HashSet<String>();
        if(roleId == null){
            return permissions;
        }

        BackendPermissionExample example = new BackendPermissionExample();
        example.or().andRoleIdEqualTo(roleId).andDeletedEqualTo(false);
        List<BackendPermission> permissionList = permissionMapper.selectByExample(example);

        for(BackendPermission permission : permissionList){
            permissions.add(permission.getPermission());
        }

        return permissions;
    }

    public boolean checkSuperPermission(Integer roleId) {
        if(roleId == null){
            return false;
        }

        BackendPermissionExample example = new BackendPermissionExample();
        example.or().andRoleIdEqualTo(roleId).andPermissionEqualTo("*").andDeletedEqualTo(false);
        return permissionMapper.countByExample(example) != 0;
    }

    public void deleteByRoleId(Integer roleId) {
        BackendPermissionExample example = new BackendPermissionExample();
        example.or().andRoleIdEqualTo(roleId).andDeletedEqualTo(false);
        permissionMapper.logicalDeleteByExample(example);
    }

    public void add(BackendPermission backendPermission) {
        backendPermission.setAddTime(LocalDateTime.now());
        backendPermission.setUpdateTime(LocalDateTime.now());
        permissionMapper.insertSelective(backendPermission);
    }
}
