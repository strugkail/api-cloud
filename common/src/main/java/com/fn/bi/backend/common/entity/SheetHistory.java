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
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SheetHistory extends FatherSheet implements Serializable {

    public static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    public Integer id;


    /**
     * sheet 创建者
     */
    @TableField(fill = FieldFill.INSERT)
    public Integer createUser;

    /**
     * sheet 更新者
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    public Integer updateUser;

    /**
     * 数据添加时间 yyyy-MM-dd HH:mm:ss
     */
    @TableField(fill = FieldFill.INSERT)
    public LocalDateTime createTime;

    /**
     * 数据更新时间 yyyy-MM-dd HH:mm:ss
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    public LocalDateTime updateTime;

    private Boolean status;
}
