package com.fn.bi.report.permission.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fn.bi.backend.common.dto.Result;
import com.fn.bi.backend.common.entity.Role;
import com.fn.bi.backend.common.entity.User;
import com.fn.bi.backend.common.entity.UserRole;
import com.fn.bi.report.permission.service.RoleService;
import com.fn.bi.report.permission.service.UserRoleService;
import com.fn.bi.report.permission.service.UserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author gyf
 * @since 2020-11-25
 */
@RestController
@RequestMapping("/permission/user")
public class UserController {
    @Resource
    private UserRoleService userRoleService;
    @Resource
    private UserService userService;
    @Resource
    private RoleService roleService;

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

    @GetMapping("/adminList")
    public Result<Page<User>> adminList(Page<User> page, User userQuery) {
        LambdaQueryWrapper<User> wrapper = Wrappers.<User>lambdaQuery().orderByDesc(User::getIsAdmin);
        if (!StringUtils.isEmpty(userQuery.getEnName())) wrapper.like(User::getEnName, userQuery.getEnName());
        Page<User> userPage = userService.page(page, wrapper);
        for (User user : userPage.getRecords()) {
            user.setRoles(roleService.getRoleByUserId(user.getId()));
        }
        return Result.ok(userPage);
    }

//    @PostMapping("/updateAdmin")
//    @Transactional
//    public Result<?> updateAdmin(@RequestBody Admin admin) {
//        //修改名称
//        userService.updateById(new User().setId(admin.getId()).setCnName(admin.getCnName()));
//        userRoleService.remove(Wrappers.<UserRole>lambdaQuery().eq(UserRole::getUserId, admin.getId()));
//        if (!StringUtils.isEmpty(admin.getRoleId())) {
//            String[] split = admin.getRoleId().split(",");
//            for (String roleId : split) {
//                if (!StringUtils.isEmpty(roleId))
//                    userRoleService.save(new UserRole().setRoleId(Long.valueOf(roleId)).setUserId(Long.valueOf(admin.getId())));
//            }
//        }
//        return Result.ok();
//    }

    @PostMapping("/updateAdmin")
    @Transactional
    public Result<?> updateAdmin(@RequestBody User user) {
        //修改名称
        User updateUser = new User().setId(user.getId()).setCnName(user.getCnName());
        if(user.getRoles().size()!=0) updateUser.setIsAdmin(true);
        else updateUser.setIsAdmin(false);
        userService.updateById(updateUser);
        userRoleService.remove(Wrappers.<UserRole>lambdaQuery().eq(UserRole::getUserId, user.getId()));
        for (Role role : user.getRoles()) {
            if (!StringUtils.isEmpty(role))
                userRoleService.save(new UserRole().setRoleId(role.getId()).setUserId(Long.valueOf(user.getId())));
        }
        return Result.ok();
    }
}
