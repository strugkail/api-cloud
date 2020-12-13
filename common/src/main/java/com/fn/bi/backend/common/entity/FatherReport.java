package com.fn.bi.backend.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class FatherReport<T extends FatherSheet> {

    /**
     * 所有sheet对象
     */
    @TableField(exist = false)
    public List<T> sheets;

    public static final long serialVersionUID = 1L;

    /**
     * 主键 id
     */
    @TableId(value = "id", type = IdType.AUTO)
    public Integer id;

    /**
     * 报表 id
     */
    public Integer reportId;

//    /**
//     * mstr报表使用 guid
//     */
//    public String mstrGuid;

    /**
     * 报表名称
     */
    public String reportName;

    /**
     * 报表格式：默认（单数据块）  回购率(多数据块,固定行数） 合并(指定列相邻行相同数据的合并）
     */
    public String reportForm;

    /**
     * id报表周期：日报 周报 月报
     */
    public String reportCycle;

    /**
     * 报表发送邮件的标题名称
     */
    public String reportTitle;

    /**
     * 业务模式 B2C B2B
     */
    public String businessModel;

    /**
     * 门店类型：AC RT RTAC
     */
    public String storeType;

    /**
     * 报表生成类型 JAVA MSTR
     */
    public String generateType;

    /**
     * 文件类型（后缀名） .xlsx .pdf
     */
    public String fileType;

    /**
     * 多附件id 多个用 , 分隔
     */
    public String enclosureId;

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
     * 排序编号
     */
    public Integer sortNo;

    /**
     * 是否为多附件：1，是   2，否
     */
    public Boolean isEnclosures;

    /**
     * 是否是核心报表 1 是 0 非
     */
    public Boolean isCore;

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
