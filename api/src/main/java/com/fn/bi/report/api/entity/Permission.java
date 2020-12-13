package com.fn.bi.report.api.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 权限表
 * </p>
 *
 * @author gyf
 * @since 2020-11-25
 */
@Data
@EqualsAndHashCode
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 父权限
     */
    private Long parentId;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 权限英文名称
     */
    private String en_name;

    /**
     * 授权路径
     */
    private String url;

    /**
     * 备注
     */
    private String description;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
