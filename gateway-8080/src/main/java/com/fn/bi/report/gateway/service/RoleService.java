//package com.feiniu.backend.gateway.service;
//
//import com.baomidou.mybatisplus.core.toolkit.Wrappers;
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.feiniu.backend.gateway.entity.Role;
//import com.feiniu.backend.gateway.mapper.RoleMapper;
//import org.apache.ibatis.annotations.Param;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//import java.util.List;
//
///**
// * <p>
// * 角色表 服务实现类
// * </p>
// *
// * @author gyf
// * @since 2020-11-25
// */
//@Service
//public class RoleService extends ServiceImpl<RoleMapper, Role> {
//    @Resource
//    private RoleMapper roleMapper;
//
//    public List<String> getRoleByEnName(@Param("enName") String enName) {
//        return roleMapper.getRoleByEnName(enName);
//    }
//
//    public List<Role> adminList() {
//        return roleMapper.selectList(null);
//    }
//}
