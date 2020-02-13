package com.huli.foxread.contact;

public interface Func {


    /**
     * 用途：使用手机唯一标识符注册/登录
     * 参数：
     * unique_id---手机唯一标识符
     */
    String USE_UNIQUE_ID_LOGIN_OR_REG = "use_unique_id_login_or_reg";

    /**
     * 用途：用户（自己）信息
     * 参数：
     * token---token
     */
    String USERS_INFO = "users_info";

    /**
     * 用途：获取广告
     * 参数：
     */
    String GET_AD = "get_advertisement";


}
