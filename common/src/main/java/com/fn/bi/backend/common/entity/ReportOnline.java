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
import java.util.List;

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
public class ReportOnline extends FatherReport<SheetOnline> implements Serializable {

    @TableField(exist = false)
    public List<SheetOnline> sheets;
    /**
     * 主键 id
     */
    @TableId(value = "id", type = IdType.AUTO)
    public Integer id;

    /**
     * 报表更新者
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    public Integer updateUser;

    /**
     * 报表创建者
     */
    @TableField(fill = FieldFill.INSERT)
    public Integer createUser;


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

}
