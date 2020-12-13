package com.fn.bi.report.develop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fn.bi.backend.common.entity.UserReport;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author gyf
 * @since 2020-10-14
 */
public interface UserReportMapper extends BaseMapper<UserReport> {

    @Update("update  user_report set isTest=1 where report_id=#{reportId} and user_id in(#{setTestAddressee})")
    int setTestAddressee(@Param("setTestAddressee") String setTestAddressee, @Param("reportId") String reportId);

    @Update("update  user_report set isProd=1 where report_id=#{reportId} and user_id in(${setProAddressee})")
    int setProAddressee(@Param("setProAddressee") String setProAddressee, @Param("reportId") String reportId);

    @Update("update  user_report set isTest=0 where report_id=#{reportId} and user_id in(${cancelTestAddressee})")
    int cancelTestAddressee(@Param("cancelTestAddressee") String cancelTestAddressee, @Param("reportId") String reportId);

    @Update("update  user_report set isProd=0 where report_id=#{reportId} and user_id in(${cancelProAddressee})")
    int cancelProAddressee(@Param("cancelProAddressee") String cancelProAddressee, @Param("reportId") String reportId);


//    @Update("update backend_user_report set isTest=1 where report_id=#{reportId} and user_id in(select id from backend_user where email_address in (${emails}))")
//    int setTestAddresseeByEmail(@Param("emails") String emails, @Param("reportId") String reportId);
//
//    @Update("update backend_user_report set isPro=1 where report_id=#{reportId} and user_id in(select id from backend_user where email_address in (${emails}))")
//    int setProAddresseeByEmail(@Param("emails") String emails, @Param("reportId") String reportId);
//
//    @Update("delete from backend_user_report where report_id = #{reportId} and isTest = 1 and user_id in (select id from backend_user where email_address in (${emails}))")
//    int delTestAddressByEmail(@Param("emails") String emails, @Param("reportId") String reportId);
//
//    @Update("delete from backend_user_report where report_id = #{reportId} and isPro = 1 and user_id in (select id from backend_user where email_address in (${emails}))")
//    int delProAddressByEmail(@Param("emails") String emails, @Param("reportId") String reportId);
}
