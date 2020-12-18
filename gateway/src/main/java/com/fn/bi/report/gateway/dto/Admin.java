package com.fn.bi.report.gateway.dto;

import lombok.Data;

import java.util.List;

@Data
public class Admin {
    private Integer id;
    private String cnName;
    private String password;
    private String enName;
    private String roleId;
    private String roleName;
    private List<Long> permissions;
}
