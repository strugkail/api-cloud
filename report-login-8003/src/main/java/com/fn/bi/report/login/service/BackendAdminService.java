package com.fn.bi.report.login.service;


import com.fn.bi.report.login.entity.BackendAdmin;
import com.fn.bi.report.login.entity.BackendAdminExample;
import com.fn.bi.report.login.mapper.generator.BackendAdminMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BackendAdminService {
    private final BackendAdmin.Column[] result = new BackendAdmin.Column[]{BackendAdmin.Column.id, BackendAdmin.Column.username, BackendAdmin.Column.avatar, BackendAdmin.Column.roleIds};
    @Resource
    private BackendAdminMapper adminMapper;

    public List<BackendAdmin> findAdmin(String username) {
        BackendAdminExample example = new BackendAdminExample();
        example.or().andUsernameEqualTo(username).andDeletedEqualTo(false);
        return adminMapper.selectByExample(example);
    }

    public BackendAdmin findAdmin(Integer id) {
        return adminMapper.selectByPrimaryKey(id);
    }

    public PageInfo<BackendAdmin> querySelective(String userName, Integer page, Integer limit, String sort, String order) {
        BackendAdminExample example = new BackendAdminExample();
        BackendAdminExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(userName)) {
            criteria.andUsernameLike("%" + userName + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        return PageHelper.startPage(page, limit).doSelectPageInfo(()->adminMapper.selectByExampleSelective(example, result));
    }

    public int updateById(BackendAdmin admin) {
        admin.setUpdateTime(LocalDateTime.now());
        return adminMapper.updateByPrimaryKeySelective(admin);
    }

    public void deleteById(Integer id) {
        adminMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(BackendAdmin admin) {
        admin.setAddTime(LocalDateTime.now());
        admin.setUpdateTime(LocalDateTime.now());
        adminMapper.insertSelective(admin);
    }

    public BackendAdmin findById(Integer id) {
        return adminMapper.selectByPrimaryKeySelective(id, result);
    }

    public List<BackendAdmin> all() {
        BackendAdminExample example = new BackendAdminExample();
        example.or().andDeletedEqualTo(false);
        return adminMapper.selectByExample(example);
    }
}
