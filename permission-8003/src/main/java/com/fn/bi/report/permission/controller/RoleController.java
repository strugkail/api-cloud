package com.fn.bi.report.permission.controller;


import com.fn.bi.backend.common.dto.Result;
import com.fn.bi.backend.common.entity.Role;
import com.fn.bi.report.permission.service.RoleService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/permission/role")
public class RoleController {
    @Resource
    private RoleService roleService;

    @GetMapping("/list")
    public Result<List<Role>> roleList() {
        return Result.ok(roleService.list());
    }

    @PostMapping("/update")
    public Result<List<Role>> update(@RequestBody Role role) {
        return roleService.updateById(role) ? Result.ok("修改成功") : Result.error("修改失败");
    }

    @PostMapping("/del")
    public Result<?> del(@RequestBody Role role) {
        return roleService.removeById(role) ? Result.ok("删除成功") : Result.error("删除失败");
    }

}
