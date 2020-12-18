//package com.feiniu.backend.gateway.controller;
//
//import com.baomidou.mybatisplus.core.toolkit.Wrappers;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.feiniu.backend.gateway.dto.Admin;
//import com.feiniu.backend.gateway.dto.Result;
//import com.feiniu.backend.gateway.entity.Permission;
//import com.feiniu.backend.gateway.entity.RolePermission;
//import com.feiniu.backend.gateway.service.PermissionService;
//import com.feiniu.backend.gateway.service.RolePermissionService;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.*;
//
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/permission")
//public class PermissionController {
//    @Resource
//    private PermissionService permissionService;
//    @Resource
//    private RolePermissionService rolePermissionService;
//
//    private final ThreadLocal<List<Permission>> allPermission = new ThreadLocal<>();
//
//    @GetMapping("/tree")
//    public Result<Map<String, Object>> tree() {
//        allPermission.set(permissionService.list(Wrappers.<Permission>lambdaQuery().orderByAsc(Permission::getId)));
//        HashMap<String, Object> rootNode = new HashMap<>();
//        formatTree(rootNode);
//        List<Object> list = new ArrayList<>();
//        list.add(rootNode);
//        List<Long> allId = allPermission.get().stream().map(Permission::getId).collect(Collectors.toList());
//        Map<String, Object> map = new HashMap<>();
//        map.put("allTree", list);
//        map.put("allId", allId);
//        return Result.ok(map);
//    }
//
//    @GetMapping("/checkTree")
//    public Result<Map<String, Object>> checkTree(Long roleId) {
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("allTree", tree().getData().get("allTree"));
//        map.put("checked", permissionService.getCheckPermission(roleId));
//        return Result.ok(map);
//    }
//
//    @PostMapping("/updatePermission")
//    @Transactional
//    public Result<?> updatePermission(@RequestBody String body) {
//        Map<?, ?> request = null;
//        try {
//            request = new ObjectMapper().readValue(body, Map.class);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        assert request != null;
//        Long roleId = Long.valueOf((Integer) request.get("roleId"));
//        List<?> permissions = (List<?>) request.get("permissions");
//        rolePermissionService.remove(Wrappers.<RolePermission>lambdaQuery().eq(RolePermission::getRoleId, roleId));
//        ArrayList<RolePermission> rolePermissionList = new ArrayList<>();
//        for (Object pId : permissions) {
//            rolePermissionList.add(new RolePermission().setPermissionId(Long.valueOf((Integer) pId)).setRoleId(roleId));
//        }
//        rolePermissionService.saveBatch(rolePermissionList);
//        return Result.ok("修改权限成功");
//    }
//
////    @PostMapping("/updatePermission")
////    @Transactional
////    public Result<?> updatePermission(@RequestBody Admin body) {
//////        Map<?, ?> request = null;
//////        try {
//////            request = new ObjectMapper().readValue(body, Map.class);
//////        } catch (JsonProcessingException e) {
//////            e.printStackTrace();
//////        }
//////        assert request != null;
//////        Long roleId = Long.valueOf((Integer) request.get("roleId"));
//////        List<?> permissions = (List<?>) request.get("permissions");
////        rolePermissionService.remove(Wrappers.<RolePermission>lambdaQuery().eq(RolePermission::getRoleId, body.getRoleId()));
////        ArrayList<RolePermission> rolePermissionList = new ArrayList<>();
////        for (Object pId : body.getPermissions()) {
////            rolePermissionList.add(new RolePermission().setPermissionId(Long.valueOf((Integer) pId)).setRoleId(Long.valueOf(body.getRoleId())));
////        }
////        rolePermissionService.saveBatch(rolePermissionList);
////        return Result.ok("修改权限成功");
////    }
//
//    @PostMapping("/addPermission")
//    @Transactional
//    public Result<?> addPermission(Permission permission) {
//        Permission parent = permissionService.getOne(Wrappers.<Permission>lambdaQuery().eq(Permission::getId, permission.getParentId()));
//        parent.setLeafNode(false);
//        permissionService.updateById(parent);
//        permissionService.save(permission);
//        return Result.ok("菜单添加成功!");
//    }
//
//    @PostMapping("/delPermission")
//    @Transactional
//    public Result<?> delPermission(Long id) {
//        Permission parent = permissionService.getOne(Wrappers.<Permission>lambdaQuery().eq(Permission::getId, id));
//        permissionService.removeById(id);
//        List<Permission> children = permissionService.list(Wrappers.<Permission>lambdaQuery().eq(Permission::getParentId, parent.getId()));
//        if (children.size() == 0) {
//            parent.setLeafNode(true);
//            permissionService.updateById(parent);
//        }
//        return Result.ok("菜单删除成功!");
//    }
//
//
//    public void formatTree(HashMap<String, Object> parentNode) {
//        if (!parentNode.containsKey("id")) {
//            Permission permission = allPermission.get().get(0);
//            parentNode.put("id", permission.getId());
//            parentNode.put("label", permission.getName());
//            List<HashMap<String, Object>> children = new ArrayList<>();
//            parentNode.put("children", children);
//            formatTree(parentNode);
//        } else {
//            Long parentId = (Long) parentNode.get("id");
//            List<Permission> childrenList = allPermission.get().stream().filter(p -> p.getParentId().equals(parentId)).collect(Collectors.toList());
//            if (childrenList.size() == 0) return;
//            List<Object> list = (List<Object>) parentNode.get("children");
//            for (Permission permission : childrenList) {
//                HashMap<String, Object> childrenMap = new HashMap<>();
//                childrenMap.put("id", permission.getId());
//                childrenMap.put("label", permission.getName());
//                if (!permission.getLeafNode()) {
//                    List<HashMap<String, Object>> children1 = new ArrayList<>();
//                    childrenMap.put("children", children1);
//                    formatTree(childrenMap);
//                }
//                list.add(childrenMap);
//            }
//
//        }
//    }
//}
