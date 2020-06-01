package com.huli.foxread.contact;

public interface Consts {
    String DOWNLOAD_URL = "https://download.hulimedia.com/?my_invite_code=";

//        String BASE_URL = "http://testnovel.hongyutiancheng.com.cn";
//    String BASE_URL = "http://buildnovel.hongyutiancheng.com.cn";
    String BASE_URL = "http://api.hulimedia.com";
    String NOT_CPL_URL = BASE_URL + "/api/v2";

    /*帮助反馈*/
    String FEEDBACK_URL = BASE_URL + "/api/feedback";
    /*用户协议*/
    String USER_AGREEMENT_URL = BASE_URL + "/api/clause/detail?id=2";
    /*隐私策略*/
    String PRIVACY_POLICY_URL = BASE_URL + "/api/clause/detail?id=1";
    /*会员特权说明*/
    String PRIVILEGE_EXPLAIN_URL = BASE_URL + "/api/clause/detail?id=3";
    /*会员服务协议*/
    String MEMBERSHIP_AGREEMENT_URL = BASE_URL + "/api/clause/detail?id=4";
    /*邀请好友说明*/
    String INVITE_FRIENDS_EXPLAIN_URL = BASE_URL + "/api/clause/detail?id=5";
    /*签到说明*/
    String SIGN_IN_EXPLAIN_URL = BASE_URL + "/api/clause/detail?id=6";
    /*福利规则*/
    String WELFARE_RULE_URL = BASE_URL + "/api/clause/detail?id=7";

    String USE_UNIQUE_ID_LOGIN_OR_REG_API = NOT_CPL_URL + Func.USER_VISITOR_LOGIN;
    String USE_PHONE_ONEKEY_LOGIN = NOT_CPL_URL + Func.USER_ONEKEY_LOGIN;
    String USER_SET_GENDER_API = NOT_CPL_URL + Func.USER_SET_GENDER;
    String USERS_INFO_API = NOT_CPL_URL + Func.USER_INFO;
    String USER_CAPITAL_API = NOT_CPL_URL + Func.USER_CAPITAL;
    String USER_MOBILE_LOGIN_API = NOT_CPL_URL + Func.USER_MOBILE_LOGIN;
    String USER_WX_LOGIN_API = NOT_CPL_URL + Func.USER_WX_LOGIN;
    String USER_LOGOUT_API = NOT_CPL_URL + Func.USER_LOGOUT;
    String SMS_SEND_API = NOT_CPL_URL + Func.SMS_SEND;
    String BINDING_WECHAT_API = NOT_CPL_URL + Func.BINDING_WECHAT;
    String BINDING_PHONE_API = NOT_CPL_URL + Func.BINDING_PHONE;
    String CHANGE_BIND_MOBILE_API = NOT_CPL_URL + Func.CHANGE_BIND_MOBILE;
    String UNBIND_MOBILE_API = NOT_CPL_URL + Func.UNBIND_MOBILE;
    String SET_USER_PROFILE_API = NOT_CPL_URL + Func.USER_PROFILE;

    String MSG_LIST_API = NOT_CPL_URL + Func.MSG_LIST;

    String ADS_TAIL_API = NOT_CPL_URL + Func.ADS_TAIL;
    String ADS_BANNER_API = NOT_CPL_URL + Func.ADS_BANNER;
    String ADS_PLAQUE_API = NOT_CPL_URL + Func.ADS_PLAQUE;
    String ADS_INFO_API = NOT_CPL_URL + Func.ADS_INFO;

    String AVATAR_LIST_API = NOT_CPL_URL + Func.AVATAR_LIST;

    String INDEX_PAGE_API = NOT_CPL_URL + Func.INDEX_PAGE;
    String BANNER_READ_API = NOT_CPL_URL + Func.BANNER_READ;
    String NOVEL_POPULAR_API = NOT_CPL_URL + Func.NOVEL_POPULAR;
    String PREFER_READ_API = NOT_CPL_URL + Func.PREFER_READ;
    String POPULAR_RANKING_API = NOT_CPL_URL + Func.POPULAR_RANKING;
    String POPULAR_TIME_API = NOT_CPL_URL + Func.POPULAR_TIME;
    String NOVEL_CATEGORY_ALL_API = NOT_CPL_URL + Func.NOVEL_CATEGORY_ALL;
    String NOVEL_CATEGORY_API = NOT_CPL_URL + Func.NOVEL_CATEGORY;
    String NOVEL_CATEGORY_SUB_API = NOT_CPL_URL + Func.NOVEL_CATEGORY_SUB;
    String NOVEL_CHOICE_SUPERIOR_API = NOT_CPL_URL + Func.NOVEL_CHOICE_SUPERIOR;
    String NOVEL_CHOICE_API = NOT_CPL_URL + Func.NOVEL_CHOICE;
    String SEARCH_NOVEL_API = NOT_CPL_URL + Func.SEARCH_NOVEL;
    String NOVEL_HOT_API = NOT_CPL_URL + Func.NOVEL_HOT;
    String HOT_KEYWORD_API = NOT_CPL_URL + Func.HOT_KEYWORD;

    String NOVEL_COLUMN_SELECTED_API = NOT_CPL_URL + Func.NOVEL_COLUMN_SELECTED;
    String NOVEL_COLUMN_BOY_API = NOT_CPL_URL + Func.NOVEL_COLUMN_BOY;
    String NOVEL_COLUMN_GIRL_API = NOT_CPL_URL + Func.NOVEL_COLUMN_GIRL;

    String NOVEL_DETAILS_API = NOT_CPL_URL + Func.NOVEL_DETAILS;
    String NOVEL_CONTENT_API = NOT_CPL_URL + Func.NOVEL_CONTENT;
    String NOVEL_NOMINATE_API = NOT_CPL_URL + Func.NOVEL_NOMINATE;

    String USER_READ_TIME_API = NOT_CPL_URL + Func.USER_READ_TIME;

    String OPINION_CATEGORY_API = NOT_CPL_URL + Func.OPINION_CATEGORY;
    String OPINION_CREATE_API = NOT_CPL_URL + Func.OPINION_CREATE;


    String BOOKRACK_ADD_API = NOT_CPL_URL + Func.BOOKRACK_ADD;
    String BOOKRACK_ADD_BATCH_API = NOT_CPL_URL + Func.BOOKRACK_ADD_BATCH;
    String BOOKRACK_DEL_API = NOT_CPL_URL + Func.BOOKRACK_DEL;
    String BOOKRACK_GETLIST_API = NOT_CPL_URL + Func.BOOKRACK_GETLIST;

    String SPECIAL_BOOK_API = NOT_CPL_URL + Func.SPECIAL_BOOK;

    String NOVEL_NOVELCHAPTERLIST_API = NOT_CPL_URL + Func.NOVEL_NOVELCHAPTERLIST;

    String RECORD_DURATION_API = NOT_CPL_URL + Func.RECORD_DURATION;
    String RECORD_CREATE_API = NOT_CPL_URL + Func.RECORD_CREATE;
    String RECORD_READ_API = NOT_CPL_URL + Func.RECORD_READ;
    String RECORD_DELETE_API = NOT_CPL_URL + Func.RECORD_DELETE;


    String READ_NOVEL_RECORD_API = NOT_CPL_URL + Func.READ_NOVEL_RECORD;


    String WELFARE_LIST_API = NOT_CPL_URL + Func.WELFARE_LIST;
    String WELFARE_COMPLETE_API = NOT_CPL_URL + Func.WELFARE_COMPLETE;
    String WELFARE_COMPLETE_ADVERT_API = NOT_CPL_URL + Func.WELFARE_COMPLETE_ADVERT;
    String WELFARE_USERLIST_API = NOT_CPL_URL + Func.WELFARE_USERLIST;
    String WELFARE_SIGNIN_API = NOT_CPL_URL + Func.WELFARE_SIGNIN_INFO;
    String WELFARE_COMPLETESINGIN_API = NOT_CPL_URL + Func.WELFARE_COMPLETESINGIN;


    /*提现*/
    String WITHDRAWAL_MENU_API = NOT_CPL_URL + Func.WITHDRAWAL_MENU;
    String WITHDRAWAL_FARE_API = NOT_CPL_URL + Func.WITHDRAWAL_FARE;
    String WITHDRAWAL_SCORE_API = NOT_CPL_URL + Func.WITHDRAWAL_SCORE;
    String WITHDRAWAL_MONEY_API = NOT_CPL_URL + Func.WITHDRAWAL_MONEY;
    String WITHDRAWAL_RECORD_API = NOT_CPL_URL + Func.WITHDRAWAL_RECORD;
    /*提现*/

    /*我的金币相关*/
    String GOLD_EARNINGS_LIST_API = NOT_CPL_URL + Func.GOLD_EARNINGS_LIST;
    /*我的金币相关*/
    //绑银行卡
    String BANK_CREATE_API = NOT_CPL_URL + Func.BANK_CREATE;


    //邀请好友页面信息
    String WELFARE_INVITE_API = NOT_CPL_URL + Func.WELFARE_INVITE;
    //填写邀请码
    String FILLIN_INVITE_CODE_API = NOT_CPL_URL + Func.FILLIN_INVITE_CODE;
    /*已邀好友*/
    String INVITATION_INDEX_API = NOT_CPL_URL + Func.INVITATION_INDEX;


    /*会员充值套餐*/
    String ORDER_RECHARGE_API = NOT_CPL_URL + Func.ORDER_RECHARGE;
    /*创建订单*/
    String ORDER_CREATE_API = NOT_CPL_URL + Func.ORDER_CREATE;
    String PAY_WECHAT_API = NOT_CPL_URL + Func.PAY_WECHAT;
    String PAY_ALIPAY_API = NOT_CPL_URL + Func.PAY_ALIPAY;

    /*检测更新*/
    String VERSION_CHECK_API = NOT_CPL_URL + Func.VERSION_CHECK;
    /*APP版本详情*/
    String VERSION_DETAIL_API = NOT_CPL_URL + Func.VERSION_DETAIL;

    /*书评*/
    String APPRAISE_CREATE_API = NOT_CPL_URL + Func.APPRAISE_CREATE;
    String APPRAISE_LIST_API = NOT_CPL_URL + Func.APPRAISE_LIST;
    String APPRAISE_LIKE_API = NOT_CPL_URL + Func.APPRAISE_LIKE;


    /***event***/
    String SMS_REGISTER = "register";   //用于注册EVENT
    String SMS_LOGIN = "login";         //用于手机号登录EVENT
    String SMS_UNTYING = "untying";     //用于(更换绑定手机)EVENT
    String SMS_BIND = "bind";           //用于(绑定手机)EVENT
    /***event***/

    /***书城tab***/
    int TYPE_SELECTION = 0;
    int TYPE_BOY = 1;
    int TYPE_GIRL = 2;
    /***书城tab***/

    int TYPE_WELFARE = 5;

    /***排行榜tab***/
    int RANK_TYPE_HOT = 1;
    int RANK_TYPE_END = 2;
    int RANK_TYPE_DARK_HORSE = 3;
    int RANK_TYPE_HOT_BOT = 4;
    /***排行榜tab***/

    int TYPE_NEWBOOK = 1;
    int TYPE_ENDBOOK = 2;

    int DEVICE_ANDROID = 1;
    int DEVICE_IOS = 2;

    //*************************Param key******************************

    String FACILITY = "facility";
    String APK_CHANNEL = "apk_channel";
    String VERSION_CODE = "versionCode";

    String TOKEN = "token";
    String VER = "version";             //版本号，加上这个参数，以防止没有参数时无法访问接口****
    String UNIQUE_ID = "identifier";    //手机唯一标识符
    String MOBILE = "mobile";           //手机号
    String CAPTCHA = "captcha";         //验证码
    String EVENT = "event";             //事件,event=register(登录/注册)

    String USERNAME = "username";
    String GENDER = "gender";
    String AVATAR = "avatar";

    String CODE = "code";
    String UNIONID = "unionid";
    String OPENID = "openid";

    String MOBILE_CAPTCHA = "mobile_captcha";        //验证码

    String CATEGORY = "category";
    String TYPE = "type";
    String RANK_FORM_BG = "form_boy_girl";
    String TYPE_RANK = "type_rank";

    String NOVEL_ID = "id";
    String NOVEL_IDS = "ids";

    String BOOK_ID = "novel_id";
    String CHAPTER_ID = "chapter_id";
    String CHAPTER_NAME = "chapter_name";
    String CHAPTER = "chapter";

    String CAT_PID = "id";
    String CAT_ID = "classify_id";
    String CAT_IS_PARENT = "is_parent";
    String CAT_WORD_NUM = "word_calssify";
    String CAT_IS_END = "is_end";
    String CAT_STATUS = "status";

    String CAT_SECOND_CLASSIFY_ID = "second_classify_id";
    String CAT_BOY_GIRL = "type";

    String FILTRATE_KEYWORD = "keyword";

    String PAGE = "page";
    String PAGE_SIZE = "page_size";

    String WELFARE_READ_ID = "welfare_read_id";

    String POSITION = "position";

    String PHONE = "phone";
    String OPINION_CATEGORY_ID = "opinion_category_id";
    String CONTENT = "content";

    String WITHDRAWAL_PLAN_ID = "id";

    /*银行卡*/
    String ID_CARD_NUMBER = "id_card";
    String BANK_NAME = "bank_name";
    String BANK_ADDRESS = "address";
    String BANK_ACCOUNT = "account";
    String CARDHOLDER_NAME = "name";
    /*银行卡*/

    String VIP_COMBO_ID = "id";

    String MISSION_ID = "id";
    String SUB_MISSION_ID = "lower_id";

    String ORDER_ID = "order_id";

    String REVIEW_ID = "id";
    String PREFER = "prefer";
    String SCORE = "score";

}
