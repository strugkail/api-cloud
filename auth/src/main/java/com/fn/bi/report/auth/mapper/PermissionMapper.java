package com.fn.bi.report.auth.mapper;


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

}
