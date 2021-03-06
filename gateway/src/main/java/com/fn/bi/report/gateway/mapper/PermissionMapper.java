package com.fn.bi.report.gateway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fn.bi.backend.common.entity.Permission;
import org.apache.ibatis.annotations.Param;
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
public interface PermissionMapper extends BaseMapper<Permission> {
    @Select("SELECT p.`url` FROM `role_permission` rp \n" +
            "INNER JOIN `permission` p ON p.`id`=rp.`permission_id`\n" +
            "WHERE rp.`role_id` IN (\n" +
            "SELECT ur.`role_id` FROM `user` u\n" +
            "LEFT JOIN `user_role` ur ON u.`id`=ur.`user_id` WHERE u.`en_name`=#{enName}\n" +
            ")")
    List<String> getUrlByEnName(@Param("enName") String enName);


    @Select("SELECT p.`id`,p.`name`,p.`en_name` FROM `role` r\n" +
            "LEFT JOIN `role_permission` rp ON r.`id`=rp.`role_id`\n" +
            "LEFT JOIN `permission` p ON rp.`permission_id`=p.`id`\n" +
            "WHERE r.`id`=#{roleId}")
    List<Long> getCheckPermission(@Param("roleId") Long roleId);

    @Select("select ip from white_list")
    List<String> getWhiteList();
}
