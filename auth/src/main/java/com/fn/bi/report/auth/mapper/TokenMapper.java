package com.fn.bi.report.auth.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface TokenMapper {

    @Delete("DELETE FROM `oauth_access_token` WHERE `user_name`= #{enName}")
    void delAccessToken(@Param("enName") String enName);

    @Select("SELECT `refresh_token` FROM `oauth_access_token` WHERE `user_name`= #{enName}")
    String getRefreshToken(@Param("enName") String enName);

    @Delete("DELETE FROM `oauth_refresh_token` WHERE `token_id` = #{accessToken}")
    void delRefreshToken(@Param("accessToken") String accessToken);
}
