package com.fn.bi.report.api.mapper;

import com.fn.bi.report.api.entity.Permission;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 权限表 Mapper 接口
 * </p>
 *
 * @author gyf
 * @since 2020-11-25
 */
public interface PermissionMapper {
    @Select("SELECT en_name,url FROM permission")
    List<Permission> getPermission();
}
