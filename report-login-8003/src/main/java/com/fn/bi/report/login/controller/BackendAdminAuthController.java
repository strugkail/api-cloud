package com.fn.bi.report.login.controller;


import com.fn.bi.report.login.entity.BackendAdmin;
import com.fn.bi.report.login.comUtil.JacksonUtil;
import com.fn.bi.backend.common.dto.Result;
import com.fn.bi.backend.common.ienum.StatusCode;
import com.fn.bi.report.login.service.BackendAdminService;
import com.fn.bi.report.login.service.BackendPermissionService;
import com.fn.bi.report.login.service.BackendRoleService;
import com.fn.bi.report.login.util.IpUtil;
import com.fn.bi.report.login.util.Permission;
import com.fn.bi.report.login.util.PermissionUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/admin/auth")
@Validated
public class BackendAdminAuthController {
    private final Log logger = LogFactory.getLog(BackendAdminAuthController.class);

    @Autowired
    private BackendAdminService adminService;
    @Autowired
    private BackendRoleService roleService;
    @Autowired
    private BackendPermissionService permissionService;
    /*
     *  { username : value, password : value }
     */

    @PostMapping("/login")
    public Object login(
                         @RequestBody String body, HttpServletRequest request) {
        String username = JacksonUtil.parseString(body, "username");
        String password = JacksonUtil.parseString(body, "password");

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return Result.badArgument();
        }

        Subject currentUser = SecurityUtils.getSubject();
        try {
            currentUser.login(new UsernamePasswordToken(username, password));
        } catch (UnknownAccountException uae) {
            logger.error("登录用户帐号或密码不正确");
            return Result.error(StatusCode.passWordErrno, "用户帐号或密码不正确");
        } catch (LockedAccountException lae) {
            logger.error("登录用户帐号已锁定不可用");
            return Result.error(StatusCode.userLocked, "用户帐号已锁定不可用");

        } catch (AuthenticationException ae) {
            logger.error("登录认证失败");
            return Result.error(StatusCode.passWordErrno, "认证失败");
        }

        currentUser = SecurityUtils.getSubject();
        BackendAdmin admin = (BackendAdmin) currentUser.getPrincipal();
        admin.setLastLoginIp(IpUtil.getIpAddr(request));
        admin.setLastLoginTime(LocalDateTime.now());
        adminService.updateById(admin);

        logger.info("登录");

        // userInfo
        Map<String, Object> adminInfo = new HashMap<String, Object>();
        adminInfo.put("nickName", admin.getUsername());
        adminInfo.put("avatar", admin.getAvatar());

        Map<Object, Object> result = new HashMap<Object, Object>();
        result.put("token", currentUser.getSession().getId());
        result.put("adminInfo", adminInfo);
        result.put("backendUserId", admin.getId());
        return Result.ok(result);
    }

    /*
     *
     */
    @RequiresAuthentication
    @PostMapping("/logout")
    public Object logout() {
        Subject currentUser = SecurityUtils.getSubject();

        logger.info("退出");
        currentUser.logout();
        return Result.ok();
    }


    @RequiresAuthentication
    @GetMapping("/info")
    public Object info() {
        Subject currentUser = SecurityUtils.getSubject();
        BackendAdmin admin = (BackendAdmin) currentUser.getPrincipal();

        Map<String, Object> data = new HashMap<>();
        data.put("name", admin.getUsername());
        data.put("avatar", admin.getAvatar());

        Integer[] roleIds = admin.getRoleIds();
        Set<String> roles = roleService.queryByIds(roleIds);
        Set<String> permissions = permissionService.queryByRoleIds(roleIds);
        data.put("roles", roles);
        // NOTE
        // 这里需要转换perms结构，因为对于前端而已API形式的权限更容易理解
        data.put("perms", toApi(permissions));
        return Result.ok(data);
    }

    @Autowired
    private ApplicationContext context;
    private HashMap<String, String> systemPermissionsMap = null;

    private Collection<String> toApi(Set<String> permissions) {
        if (systemPermissionsMap == null) {
            systemPermissionsMap = new HashMap<>();
            final String basicPackage = "org.linlinjava.litemall.admin";
            List<Permission> systemPermissions = PermissionUtil.listPermission(context, basicPackage);
            for (Permission permission : systemPermissions) {
                String perm = permission.getRequiresPermissions().value()[0];
                String api = permission.getApi();
                systemPermissionsMap.put(perm, api);
            }
        }

        Collection<String> apis = new HashSet<>();
        for (String perm : permissions) {
            String api = systemPermissionsMap.get(perm);
            apis.add(api);

            if (perm.equals("*")) {
                apis.clear();
                apis.add("*");
                return apis;
                //                return systemPermissionsMap.values();

            }
        }
        return apis;
    }

    @GetMapping("/401")
    public Object page401() {
        return Result.unLogin();
    }

    @GetMapping("/index")
    public Object pageIndex() {
        return Result.ok();
    }

    @GetMapping("/403")
    public Object page403() {
        return Result.unAuthz();
    }
}
