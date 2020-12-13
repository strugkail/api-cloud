//package com.feiniu.backend.gateway.entity;
//
//import com.baomidou.mybatisplus.annotation.FieldFill;
//import com.baomidou.mybatisplus.annotation.IdType;
//import com.baomidou.mybatisplus.annotation.TableField;
//import com.baomidou.mybatisplus.annotation.TableId;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import lombok.experimental.Accessors;
//
//import java.io.Serializable;
//import java.time.LocalDateTime;
//
///**
// * <p>
// *
// * </p>
// *
// * @author gyf
// * @since 2020-11-25
// */
//@Data
//@EqualsAndHashCode
//@Accessors(chain = true)
//public class User implements Serializable {
//
//    private static final long serialVersionUID = 1L;
//
//    @TableId(value = "id", type = IdType.AUTO)
//    private Integer id;
//
//    /**
//     * 英文名称 域账户名称
//     */
//    private String enName;
//
//    /**
//     * 中文名
//     */
//    private String cnName;
//
//    /**
//     * 邮件地址
//     */
//    private String email;
//
//    /**
//     * 部门名称
//     */
//    private String dept;
//
//    /**
//     * 最新登录ip
//     */
//    private String lastLoginIp;
//
//    /**
//     * 删除状态 0 未删除 1 已删除
//     */
//    private Boolean deleted;
//
//    /**
//     * 创建者 id
//     */
//    @TableField(fill = FieldFill.INSERT)
//    private Integer createUser;
//
//    /**
//     * 更新者 id
//     */
//    @TableField(fill = FieldFill.INSERT_UPDATE)
//    private Integer updateUser;
//
//    /**
//     * 登陆时间 yyyy-MM-dd HH:mm:ss
//     */
//    private LocalDateTime loginTime;
//
//    private String password;
//
//    /**
//     * 创建时间 yyyy-MM-dd HH:mm:ss
//     */
//    @TableField(fill = FieldFill.INSERT)
//    private LocalDateTime createTime;
//
//    /**
//     * 更新时间 yyyy-MM-dd HH:mm:ss
//     */
//    @TableField(fill = FieldFill.INSERT_UPDATE)
//    private LocalDateTime updateTime;
//
//
//}
