package com.huli.foxread.contact;

public interface Consts {

    String BASE_URL = "http://meidavideo.luxian1992.top";
    String NOT_CPL_URL = BASE_URL + "/api/AppApi/index/";

    String USE_UNIQUE_ID_LOGIN_OR_REG_API = NOT_CPL_URL + Func.USE_UNIQUE_ID_LOGIN_OR_REG;
//    String USERS_INFO_API = NOT_CPL_URL + Func.USERS_INFO;
//    String GET_AD_API = NOT_CPL_URL + Func.GET_AD;


    String MAN = "1";
    String FEMALE = "2";

    int ZONE_GOLD_COIN = 1;
    int ZONE_ORIGINAL = 2;


    //*************************Param******************************
    String D_TOKEN = "token";
    String DATAS = "datas";             //最终的Map参数名

    String FILES = "files";
    String TOKEN = "token";
    String VER = "version";             //版本号，加上这个参数，以防止没有参数时无法访问接口****
    String UNIQUE_ID = "unique_id";     //手机唯一标识符
    String TEL = "tel";
    String PWD = "pwd";
    String VERIFY = "verify";           //验证码
    String OLD_PWD = "old_pwd";
    String NEW_PWD = "new_pwd";
    String IS_ONLINE = "is_online";
    String MONTH = "month";
    String DAY = "day";
    String NUM = "num";
    String N_ID = "id";
    String NICKNAME = "nickname";
    String GENDER = "gender";
    String SIGNATURE = "signature";
    String BIRTHDAY = "birthday";
    String CITY = "city";
    String CD_KEY = "code";
    String USER_ID = "user_id";


}
