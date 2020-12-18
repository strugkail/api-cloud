//package com.feiniu.backend.gateway.controller;
//
//
//import com.baomidou.mybatisplus.core.toolkit.Wrappers;
//import com.feiniu.backend.gateway.dto.Admin;
//import com.feiniu.backend.gateway.service.CheckTokenService;
//import com.feiniu.backend.gateway.service.PermissionService;
//import com.feiniu.backend.gateway.service.UserRoleService;
//import com.feiniu.backend.gateway.service.UserService;
//import com.fn.bi.backend.common.dto.Result;
//import com.fn.bi.backend.common.entity.User;
//import com.fn.bi.backend.common.entity.UserRole;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.*;
//
//import javax.annotation.Resource;
//import javax.validation.constraints.NotNull;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * <p>
// * 用户表 前端控制器
// * </p>
// *
// * @author gyf
// * @since 2020-11-25
// */
//@RestController
//@RequestMapping("/user")
//public class UserController {
//    @Resource
//    private UserRoleService userRoleService;
//    @Resource
//    private UserService userService;
//
//
//
//
//    @GetMapping("/adminList")
//    public Result<List<Admin>> adminList() {
//        HashMap<Integer, List<Admin>> collect = userService.adminList().stream().collect(Collectors.groupingBy(Admin::getId, HashMap::new, Collectors.toList()));
//        List<Admin> adminList = new ArrayList<>();
//        for (List<Admin> list : collect.values()) {
//            StringBuilder roles = new StringBuilder();
//            StringBuilder roleIds = new StringBuilder();
//            Admin admin1 = new Admin();
//            admin1.setCnName(list.get(0).getCnName());
//            admin1.setId(list.get(0).getId());
//            for (Admin admin : list) {
//                roles.append(admin.getRoleName()).append(",");
//                roleIds.append(admin.getRoleId()).append(",");
//            }
//            admin1.setRoleName(roles.substring(0, roles.length() - 1));
//            admin1.setRoleId(roleIds.substring(0, roleIds.length() - 1));
//            adminList.add(admin1);
//        }
//        return Result.ok(adminList);
//    }
//
//    @PostMapping("/updateAdmin")
//    @Transactional
//    public Result<?> updateAdmin(@RequestBody Admin admin) {
//        if (!StringUtils.isEmpty(admin.getCnName())) {
//            //修改名称
//            userService.updateById(new User().setId(admin.getId()).setCnName(admin.getCnName()));
//        }
//        if (!StringUtils.isEmpty(admin.getRoleId())) {
//            //删除所有角色
//            userRoleService.remove(Wrappers.<UserRole>lambdaQuery().eq(UserRole::getUserId, admin.getId()));
//            //重新添加角色
//            String[] split = admin.getRoleId().split(",");
//            for (String roleId : split) {
//                if (!StringUtils.isEmpty(roleId))
//                    userRoleService.save(new UserRole().setRoleId(Long.valueOf(roleId)).setUserId(Long.valueOf(admin.getId())));
//            }
//        }
//        return Result.ok();
//    }
//}
