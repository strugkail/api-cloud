package com.fn.bi.report.gateway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fn.bi.report.gateway.dto.Admin;
import com.fn.bi.backend.common.entity.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author gyf
 * @since 2020-11-25
 */
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT u.`en_name`,u.`id`,u.`cn_name`,r.`id` AS roleId,r.`name` AS roleName FROM `user` u \n" +
            "LEFT JOIN `user_role` ur ON u.`id`=ur.`user_id`\n" +
            "LEFT JOIN `role` r ON r.`id`=ur.`role_id`")
    List<Admin> adminList();

}
