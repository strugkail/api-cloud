package com.fn.bi.backend.common.config.mybatisplus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.fn.bi.backend.common.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class MybatisInsert implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        Integer userId = HttpUtil.getUserId();
        setFieldValByName("createTime", now, metaObject);
        setFieldValByName("updateTime", now, metaObject);
        setFieldValByName("createUser", userId, metaObject);
        setFieldValByName("updateUser", userId, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        setFieldValByName("updateUser", HttpUtil.getUserId(), metaObject);
//        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now()); // 起始版本 3.3.0(推荐)
        // 或者
//        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class); // 起始版本 3.3.3(推荐)
        // 或者
//        this.fillStrategy(metaObject, "updateTime", LocalDateTime.now()); // 也可以使用(3.3.0 该方法有bug)
    }



}
