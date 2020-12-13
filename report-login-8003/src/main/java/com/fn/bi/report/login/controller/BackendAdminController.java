package com.fn.bi.report.login.controller;

import com.fn.bi.report.login.entity.BackendAdmin;
import com.fn.bi.report.login.comUtil.RegexUtil;
import com.fn.bi.backend.common.dto.Result;
import com.fn.bi.report.login.comUtil.bcrypt.BCryptPasswordEncoder;
import com.fn.bi.report.login.validator.Order;
import com.fn.bi.report.login.validator.Sort;
import com.fn.bi.backend.common.ienum.StatusCode;
import com.fn.bi.report.login.annotation.RequiresPermissionsDesc;
import com.fn.bi.report.login.service.BackendAdminService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/admin/admin")
@Validated
public class BackendAdminController {
    private final Log logger = LogFactory.getLog(BackendAdminController.class);

    @Autowired
    private BackendAdminService adminService;

    @RequiresPermissions("admin:admin:list")
    @RequiresPermissionsDesc(menu = {"系统管理", "管理员管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(String userName,
                        @RequestParam(defaultValue = "1") Integer page,
                        @RequestParam(defaultValue = "10") Integer limit,
                        @Sort @RequestParam(defaultValue = "add_time") String sort,
                        @Order @RequestParam(defaultValue = "desc") String order) {
        return Result.ok(adminService.querySelective(userName, page, limit, sort, order));
    }

    private Object validate(BackendAdmin admin) {
        String name = admin.getUsername();
        if (StringUtils.isEmpty(name)) {
            return Result.badArgument();
        }
        if (!RegexUtil.isUsername(name)) {
            return Result.error(StatusCode.badArgumentValue, "管理员名称不符合规定");
        }
        String password = admin.getPassword();
        if (StringUtils.isEmpty(password) || password.length() < 6) {
            return Result.error(StatusCode.badArgumentValue, "管理员密码长度不能小于6");
        }
        return null;
    }

    @RequiresPermissions("admin:admin:create")
    @RequiresPermissionsDesc(menu = {"系统管理", "管理员管理"}, button = "添加")
    @PostMapping("/create")
    public Object create(@RequestBody BackendAdmin admin) {
        Object error = validate(admin);
        if (error != null) {
            return error;
        }

        String username = admin.getUsername();
        List<BackendAdmin> adminList = adminService.findAdmin(username);
        if (adminList.size() > 0) {
            return Result.error(StatusCode.dataHas, "管理员已经存在");
        }

        String rawPassword = admin.getPassword();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(rawPassword);
        admin.setPassword(encodedPassword);
        adminService.add(admin);
        logger.info("添加管理员" + username);
        return Result.ok(admin);
    }

    @RequiresPermissions("admin:admin:read")
    @RequiresPermissionsDesc(menu = {"系统管理", "管理员管理"}, button = "详情")
    @GetMapping("/read")
    public Object read(
            @NotNull Integer id) {
        BackendAdmin admin = adminService.findById(id);
        return Result.ok(admin);
    }

    @RequiresPermissions("admin:admin:update")
    @RequiresPermissionsDesc(menu = {"系统管理", "管理员管理"}, button = "编辑")
    @PostMapping("/update")
    public Object update(
            @RequestBody BackendAdmin admin) {
        Object error = validate(admin);
        if (error != null) {
            return error;
        }

        Integer anotherAdminId = admin.getId();
        if (anotherAdminId == null) {
            return Result.badArgument();
        }

        // 不允许管理员通过编辑接口修改密码
//        admin.setPassword(null);
        String rawPassword = admin.getPassword();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(rawPassword);
        admin.setPassword(encodedPassword);

        if (adminService.updateById(admin) == 0) {
            return Result.updatedDataFailed();
        }

        logger.info("编辑管理员" + admin.getUsername());
        return Result.ok(admin);
    }

    @RequiresPermissions("admin:admin:delete")
    @RequiresPermissionsDesc(menu = {"系统管理", "管理员管理"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(
            @RequestBody BackendAdmin admin) {
        Integer anotherAdminId = admin.getId();
        if (anotherAdminId == null) {
            return Result.badArgument();
        }

        // 管理员不能删除自身账号
        Subject currentUser = SecurityUtils.getSubject();
        BackendAdmin currentAdmin = (BackendAdmin) currentUser.getPrincipal();
        if (currentAdmin.getId().equals(anotherAdminId)) {
            return Result.error(StatusCode.updatedDataFailed, "管理员不能删除自己账号");
        }

        adminService.deleteById(anotherAdminId);
        logger.info("删除管理员" + admin.getUsername());
        return Result.ok();
    }
}
