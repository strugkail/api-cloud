package com.fn.bi.backend.common.ienum;


public enum StatusCode {
    success(200),   //请求成功
    fail(400),         //操作失败
    badArgument(401),   //参数格式错误
    badArgumentValue(402), //参数值不对
    notLogin(501),  //未登录
    serious(502),     //系统内部错误
    notSupport(503),    //暂不支持该业务
    updatedDateExpired(504),    //更新数据已经失效
    updatedDataFailed(505),    //数据更新失败
    notAuthz(506),        //无权限
    uploadFail(507),     //上传失败
    passWordErrno(508),     //密码错误
    userLocked(509),        //账户被锁定
    dataHas(510),        //数据已存在
    addresseeFail(511)  //设置收件人失败
    ;

    private Integer statusCode;

    StatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }
}
