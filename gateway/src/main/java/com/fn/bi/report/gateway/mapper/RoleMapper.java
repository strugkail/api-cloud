//package com.feiniu.backend.gateway.mapper;
//
//import com.baomidou.mybatisplus.core.mapper.BaseMapper;
//import com.feiniu.backend.gateway.entity.Role;
//import org.apache.ibatis.annotations.Param;
//import org.apache.ibatis.annotations.Select;
//
//import java.util.List;
//
///**
// * <p>
// * 角色表 Mapper 接口
// * </p>
// *
// * @author gyf
// * @since 2020-11-25
// */
//public interface RoleMapper extends BaseMapper<Role> {
//
//    @Select("SELECT r.`en_name` FROM `user` u LEFT JOIN `user_role` ur ON u.`id`=ur.`user_id`\n" +
//            "LEFT JOIN `role` r ON ur.`role_id`=r.`id` WHERE u.`en_name`=#{enName}")
//    List<String> getRoleByEnName(@Param("enName") String enName);
//}
