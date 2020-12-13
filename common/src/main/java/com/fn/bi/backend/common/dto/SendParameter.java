package com.fn.bi.backend.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode()
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SendParameter {
    private String reportIds; //报表ID
    private Boolean isSend;         //是否发送
    private Boolean isTest;         //false正式邮件，true测试邮件
    private Boolean isGenerate;     //0不生成，1生成
    private LocalDateTime date = LocalDateTime.now().plusDays(-1);     //日期
    private Boolean addresseeEnv = false; //正式还是测试收件人，false正式收件人，true测试收件人

}
