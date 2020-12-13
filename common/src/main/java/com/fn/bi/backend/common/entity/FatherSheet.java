package com.fn.bi.backend.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode()
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class FatherSheet {
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    public Integer id;

    /**
     * sheet id
     */
    public Integer sheetId;

    /**
     * 供前端展示使用
     */
    public String sheetName;

    /**
     * 报表对应的sheet的执行sql 多条语句用 <end> 分隔开，占位标记使用类似 $dt $db
     */
    public String sheetSql;

    /**
     * 报表里 sheet对应的要替换的 #title
     */
    public String sheetTitle;

    /**
     * 报表编号
     */
    public Integer reportId;

    /**
     * 排序编号
     */
    public String sortNo;

    /**
     * 是否已删除 1 是 0 非
     */
    @TableLogic
    public Boolean deleted;

    /**
     * 报表版本号
     */
    public Integer version;

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

}
