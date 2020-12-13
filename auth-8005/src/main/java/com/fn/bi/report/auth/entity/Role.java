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
// * 角色表
// * </p>
// *
// * @author gyf
// * @since 2020-11-25
// */
//@Data
//@EqualsAndHashCode
//public class Role implements Serializable {
//
//    private static final long serialVersionUID = 1L;
//
//    @TableId(value = "id", type = IdType.AUTO)
//    private Long id;
//
//    /**
//     * 父角色
//     */
//    private Long parentId;
//
//    /**
//     * 角色名称
//     */
//    private String name;
//
//    /**
//     * 角色英文名称
//     */
//    private String enName;
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
//
//}
