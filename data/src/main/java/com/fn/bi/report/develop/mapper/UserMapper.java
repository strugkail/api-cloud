package com.fn.bi.report.develop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fn.bi.backend.common.entity.User;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author gyf
 * @since 2020-10-14
 */
public interface UserMapper extends BaseMapper<User> {

//    @Select("select id from backend_user where\n" +
//            "id in (\n" +
//            "    select user_id from backend_user_report where report_id =#{reportId} and isTest=1\n" +
//            "        )")
//    List<Integer> getTestAddressee(String reportId);
//
//    @Select("select id from backend_user where\n" +
//            "id in (\n" +
//            "    select user_id from backend_user_report where report_id =#{reportId} and isPro=1\n" +
//            "        )")
//    List<Integer> getProAddressee(String reportId);
//
//    @Select("select id,user_name from backend_user")
//    List<User> selectAll();
}
