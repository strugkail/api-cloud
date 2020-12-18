package com.fn.bi.report.gateway.config;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fn.bi.backend.common.entity.Permission;
import com.fn.bi.report.gateway.service.PermissionService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;

@Component
public class ApplicationListener implements ApplicationRunner {
    @Resource
    private PermissionService permissionService;

    public static final HashSet<String> permissions = new HashSet<>();

    public static final HashSet<String> whiteList = new HashSet<>();


    @Override
    public void run(ApplicationArguments args) {

        List<Permission> list1 = permissionService.list(Wrappers.<Permission>lambdaQuery().select(Permission::getEnName, Permission::getUrl));
        for (Permission permission : list1) {
            permissions.add(permission.getUrl());
        }
        List<String> List = permissionService.getWhiteList();
        whiteList.addAll(List);
    }
}
