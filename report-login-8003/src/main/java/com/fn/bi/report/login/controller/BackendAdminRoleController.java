package com.fn.bi.report.login.controller;


import com.fn.bi.report.login.entity.BackendAdmin;
import com.fn.bi.report.login.entity.BackendPermission;
import com.fn.bi.report.login.entity.BackendRole;
import com.fn.bi.report.login.comUtil.JacksonUtil;
import com.fn.bi.backend.common.dto.Result;
import com.fn.bi.report.login.validator.Order;
import com.fn.bi.report.login.validator.Sort;
import com.fn.bi.backend.common.ienum.StatusCode;
import com.fn.bi.report.login.annotation.RequiresPermissionsDesc;
import com.fn.bi.report.login.service.BackendAdminService;
import com.fn.bi.report.login.service.BackendPermissionService;
import com.fn.bi.report.login.service.BackendRoleService;
import com.fn.bi.report.login.util.Permission;
import com.fn.bi.report.login.util.PermissionUtil;
import com.fn.bi.report.login.vo.PermVo;
import com.github.pagehelper.PageInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.*;

@RestController
@RequestMapping("/admin/role")
@Validated
public class BackendAdminRoleController {
    private final Log logger = LogFactory.getLog(BackendAdminRoleController.class);

    @Autowired
    private BackendRoleService roleService;
    @Autowired
    private BackendPermissionService permissionService;
    @Autowired
    private BackendAdminService adminService;

    @RequiresPermissions("admin:role:list")
    @RequiresPermissionsDesc(menu = {"系统管理", "角色管理"}, button = "角色查询")
    @GetMapping("/list")
    public Object list(String name,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        return Result.ok(roleService.querySelective(name, page, limit, sort, order));
    }

    @GetMapping("/options")
    public Object options() {
        List<BackendRole> roleList = roleService.queryAll();

        List<Map<String, Object>> options = new ArrayList<>(roleList.size());
        for (BackendRole role : roleList) {
            Map<String, Object> option = new HashMap<>(2);
            option.put("value", role.getId());
            option.put("label", role.getName());
            options.add(option);
        }

        return Result.ok(options);
    }

    @RequiresPermissions("admin:role:read")
    @RequiresPermissionsDesc(menu = {"系统管理", "角色管理"}, button = "角色详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        BackendRole role = roleService.findById(id);
        return Result.ok(role);
    }


    private Object validate(BackendRole role) {
        String name = role.getName();
        if (StringUtils.isEmpty(name)) {
            return Result.badArgument();
        }

        return null;
    }

    @RequiresPermissions("admin:role:create")
    @RequiresPermissionsDesc(menu = {"系统管理", "角色管理"}, button = "角色添加")
    @PostMapping("/create")
    public Object create(@RequestBody BackendRole role) {
        Object error = validate(role);
        if (error != null) {
            return error;
        }

        if (roleService.checkExist(role.getName())) {
            return Result.error(StatusCode.dataHas, "角色已经存在");
        }

        roleService.add(role);

        return Result.ok(role);
    }

    @RequiresPermissions("admin:role:update")
    @RequiresPermissionsDesc(menu = {"系统管理", "角色管理"}, button = "角色编辑")
    @PostMapping("/update")
    public Object update(@RequestBody BackendRole role) {
        Object error = validate(role);
        if (error != null) {
            return error;
        }

        roleService.updateById(role);
        return Result.ok();
    }

    @RequiresPermissions("admin:role:delete")
    @RequiresPermissionsDesc(menu = {"系统管理", "角色管理"}, button = "角色删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody BackendRole role) {
        Integer id = role.getId();
        if (id == null) {
            return Result.badArgument();
        }

        // 如果当前角色所对应管理员仍存在，则拒绝删除角色。
        List<BackendAdmin> adminList = adminService.all();
        for (BackendAdmin admin : adminList) {
            Integer[] roleIds = admin.getRoleIds();
            for (Integer roleId : roleIds) {
                if (id.equals(roleId)) {
                    return Result.error(StatusCode.updatedDataFailed, "当前角色存在管理员，不能删除");
                }
            }
        }

        roleService.deleteById(id);
        return Result.ok();
    }


    @Autowired
    private ApplicationContext context;
    private List<PermVo> systemPermissions = null;
    private Set<String> systemPermissionsString = null;

    private List<PermVo> getSystemPermissions() {
        final String basicPackage = "com.feiniu.bi.backend.admin";
        if (systemPermissions == null) {
            List<Permission> permissions = PermissionUtil.listPermission(context, basicPackage);
            systemPermissions = PermissionUtil.listPermVo(permissions);
            systemPermissionsString = PermissionUtil.listPermissionString(permissions);
        }
        return systemPermissions;
    }

    private Set<String> getAssignedPermissions(Integer roleId) {
        // 这里需要注意的是，如果存在超级权限*，那么这里需要转化成当前所有系统权限。
        // 之所以这么做，是因为前端不能识别超级权限，所以这里需要转换一下。
        Set<String> assignedPermissions = null;
        if (permissionService.checkSuperPermission(roleId)) {
            getSystemPermissions();
            assignedPermissions = systemPermissionsString;
        } else {
            assignedPermissions = permissionService.queryByRoleId(roleId);
        }

        return assignedPermissions;
    }

    /**
     * 管理员的权限情况
     *
     * @return 系统所有权限列表和管理员已分配权限
     */
    @RequiresPermissions("admin:role:permission:get")
    @RequiresPermissionsDesc(menu = {"系统管理", "角色管理"}, button = "权限详情")
    @GetMapping("/permissions")
    public Object getPermissions(Integer roleId) {
        List<PermVo> systemPermissions = getSystemPermissions();
        Set<String> assignedPermissions = getAssignedPermissions(roleId);

        Map<String, Object> data = new HashMap<>();
        data.put("systemPermissions", systemPermissions);
        data.put("assignedPermissions", assignedPermissions);
        return Result.ok(data);
    }


    /**
     * 更新管理员的权限
     *
     * @param body
     * @return
     */
    @RequiresPermissions("admin:role:permission:update")
    @RequiresPermissionsDesc(menu = {"系统管理", "角色管理"}, button = "权限变更")
    @PostMapping("/permissions")
    public Object updatePermissions(@RequestBody String body) {
        Integer roleId = JacksonUtil.parseInteger(body, "roleId");
        List<String> permissions = JacksonUtil.parseStringList(body, "permissions");
        if (roleId == null || permissions == null) {
            return Result.badArgument();
        }

        // 如果修改的角色是超级权限，则拒绝修改。
        if (permissionService.checkSuperPermission(roleId)) {
            return Result.error(StatusCode.updatedDataFailed, "当前角色的超级权限不能变更");
        }

        // 先删除旧的权限，再更新新的权限
        permissionService.deleteByRoleId(roleId);
        for (String permission : permissions) {
            BackendPermission backendPermission = new BackendPermission();
            backendPermission.setRoleId(roleId);
            backendPermission.setPermission(permission);
            permissionService.add(backendPermission);
        }
        return Result.ok();
    }

}
