//package com.feiniu.backend.gateway.controller;
//
//import com.feiniu.backend.gateway.dto.Result;
//import com.feiniu.backend.gateway.entity.Role;
//import com.feiniu.backend.gateway.service.RoleService;
//import org.springframework.web.bind.annotation.*;
//
//import javax.annotation.Resource;
//import java.util.List;
//
//@RestController
//@RequestMapping("/role")
//public class RoleController {
//    @Resource
//    private RoleService roleService;
//
//    @GetMapping("/list")
//    public Result<List<Role>> roleList() {
//        return Result.ok(roleService.list());
//    }
//
//    @PostMapping("/update")
//    public Result<List<Role>> update(@RequestBody Role role) {
//        return roleService.updateById(role) ? Result.ok("修改成功") : Result.error("修改失败");
//    }
//}
