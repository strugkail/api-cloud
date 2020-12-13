//package com.fn.bi.report.auth.entity;
//
//import com.baomidou.mybatisplus.annotation.IdType;
//import com.baomidou.mybatisplus.annotation.TableId;
//import java.time.LocalDateTime;
//import com.baomidou.mybatisplus.annotation.FieldFill;
//import com.baomidou.mybatisplus.annotation.TableField;
//import java.io.Serializable;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//
///**
// * <p>
// * 权限表
// * </p>
// *
// * @author gyf
// * @since 2020-11-25
// */
//@Data
//@EqualsAndHashCode
//public class Permission implements Serializable {
//
//    private static final long serialVersionUID = 1L;
//
//    @TableId(value = "id", type = IdType.AUTO)
//    private Long id;
//
//    /**
//     * 父权限
//     */
//    private Long parentId;
//
//    /**
//     * 权限名称
//     */
//    private String name;
//
//    /**
//     * 权限英文名称
//     */
//    private String enName;
//
//    /**
//     * 授权路径
//     */
//    private String url;
//
//    /**
//     * 备注
//     */
//    private String description;
//
//    @TableField(fill = FieldFill.INSERT)
//    private LocalDateTime createTime;
//
//    @TableField(fill = FieldFill.INSERT_UPDATE)
//    private LocalDateTime updateTime;
//
//    /**
//     * 是否为叶节点
//     */
//    private Boolean leafNode;
//
//}
