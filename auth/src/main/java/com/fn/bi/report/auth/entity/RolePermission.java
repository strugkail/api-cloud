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
// * 角色权限表
// * </p>
// *
// * @author gyf
// * @since 2020-11-25
// */
//@Data
//@EqualsAndHashCode
//public class RolePermission implements Serializable {
//
//    private static final long serialVersionUID = 1L;
//
//    @TableId(value = "id", type = IdType.AUTO)
//    private Long id;
//
//    /**
//     * 角色 ID
//     */
//    private Long roleId;
//
//    /**
//     * 权限 ID
//     */
//    private Long permissionId;
//
//    /**
//     *  创建时间 yyyy-MM-dd HH:mm:ss
//     */
//    @TableField(fill = FieldFill.INSERT)
//    private LocalDateTime createTime;
//
//    /**
//     * 更新时间  yyyy-MM-dd HH:mm:ss
//     */
//    private LocalDateTime updateTime;
//
//
//}
