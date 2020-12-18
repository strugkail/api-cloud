package com.fn.bi.report.permission.dto;

import lombok.Data;

import java.util.List;

@Data
public class RolePermission {
    private Long roleId;
    private List<Long> permissionIds;
}
