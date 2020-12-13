package com.fn.bi.backend.common.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author gyf
 * @since 2020-10-16
 */
@Data
@EqualsAndHashCode()
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UserReport implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户 id
     */
    private Integer userId;

    /**
     * 报表 id
     */
    private Integer reportId;

    /**
     * 0，非测试收件人   1，测试收件人
     */
    @TableField("isTest")
    private Boolean isTest;

    /**
     * 0，非正式收件人  1，正式收件人
     */
    @TableField("isProd")
    private Boolean isProd;

    /**
     * 创建者 id
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer createUser;

    /**
     * 更新者id
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Integer updateUser;

    /**
     * 数据添加时间 yyyy-MM-dd HH:mm:ss
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 数据更新时间 yyyy-MM-dd HH:mm:ss
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


}
