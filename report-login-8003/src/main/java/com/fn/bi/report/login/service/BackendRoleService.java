package com.fn.bi.report.login.service;

import com.fn.bi.report.login.entity.BackendRole;
import com.fn.bi.report.login.entity.BackendRoleExample;
import com.fn.bi.report.login.mapper.generator.BackendRoleMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BackendRoleService {
    @Resource
    private BackendRoleMapper roleMapper;


    public Set<String> queryByIds(Integer[] roleIds) {
        Set<String> roles = new HashSet<String>();
        if(roleIds.length == 0){
            return roles;
        }

        BackendRoleExample example = new BackendRoleExample();
        example.or().andIdIn(Arrays.asList(roleIds)).andEnabledEqualTo(true).andDeletedEqualTo(false);
        List<BackendRole> roleList = roleMapper.selectByExample(example);

        for(BackendRole role : roleList){
            roles.add(role.getName());
        }

        return roles;

    }

    public PageInfo<BackendRole> querySelective(String name, Integer page, Integer limit, String sort, String order) {
        BackendRoleExample example = new BackendRoleExample();
        BackendRoleExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }
        return PageHelper.startPage(page, limit).doSelectPageInfo(() -> roleMapper.selectByExample(example));
    }

    public BackendRole findById(Integer id) {
        return roleMapper.selectByPrimaryKey(id);
    }

    public void add(BackendRole role) {
        role.setAddTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        roleMapper.insertSelective(role);
    }

    public void deleteById(Integer id) {
        roleMapper.logicalDeleteByPrimaryKey(id);
    }

    public void updateById(BackendRole role) {
        role.setUpdateTime(LocalDateTime.now());
        roleMapper.updateByPrimaryKeySelective(role);
    }

    public boolean checkExist(String name) {
        BackendRoleExample example = new BackendRoleExample();
        example.or().andNameEqualTo(name).andDeletedEqualTo(false);
        return roleMapper.countByExample(example) != 0;
    }

    public List<BackendRole> queryAll() {
        BackendRoleExample example = new BackendRoleExample();
        example.or().andDeletedEqualTo(false);
        return roleMapper.selectByExample(example);
    }
}
