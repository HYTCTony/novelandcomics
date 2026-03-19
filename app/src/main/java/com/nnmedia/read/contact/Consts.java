package com.nnmedia.read.contact;

public interface Consts {

    String TIP = "dolphin_novel";

    String DOWNLOAD_URL = "";

    String NOT_CPL_URL = "/api/v2";

    String DYNAMIC_DOMAIN = "/api/v2/domain/list";

    /*广告接口*/
    String AD_TAIL_URL = NOT_CPL_URL + Func.AD_TAIL;
    String AD_BANNER_URL = NOT_CPL_URL + Func.AD_BANNER;
    String AD_BANNER_USER_URL = NOT_CPL_URL + Func.AD_BANNER_USER;
    String AD_PAGE_URL = NOT_CPL_URL + Func.AD_PLAQUE;

    /*帮助反馈*/
    String FEEDBACK_URL = Url.baseurl + "/api/feedback";
    /*用户协议*/
    String USER_AGREEMENT_URL = Url.baseurl + "/api/clause/detail?id=2";
    /*隐私策略*/
    String PRIVACY_POLICY_URL = Url.baseurl + "/api/clause/detail?id=1";
    /*会员特权说明*/
    String PRIVILEGE_EXPLAIN_URL = Url.baseurl + "/api/clause/detail?id=3";
    /*会员服务协议*/
    String MEMBERSHIP_AGREEMENT_URL = Url.baseurl + "/api/clause/detail?id=4";
    /*邀请好友说明*/
    String INVITE_FRIENDS_EXPLAIN_URL = Url.baseurl + "/api/clause/detail?id=5";
    /*签到说明*/
    String SIGN_IN_EXPLAIN_URL = Url.baseurl + "/api/clause/detail?id=6";
    /*福利规则*/
    String WELFARE_RULE_URL = Url.baseurl + "/api/clause/detail?id=7";

    String USE_UNIQUE_ID_LOGIN_OR_REG_API = NOT_CPL_URL + Func.USER_VISITOR_LOGIN;
    String USE_PHONE_ONEKEY_LOGIN = NOT_CPL_URL + Func.USER_ONEKEY_LOGIN;
    String USER_SET_GENDER_API = NOT_CPL_URL + Func.USER_SET_GENDER;
    String USER_CHACKTOKEN_API = NOT_CPL_URL + Func.USER_CHECKTOKEN;
    String USERS_INFO_API = NOT_CPL_URL + Func.USER_INFO;
    String USER_CAPITAL_API = NOT_CPL_URL + Func.USER_CAPITAL;
    String USER_MOBILE_LOGIN_API = NOT_CPL_URL + Func.USER_MOBILE_LOGIN;
    String USER_PASSWORD_LOGIN_API = NOT_CPL_URL + Func.USER_PASSWORD_LOGIN;
    String USER_CHANGE_PASSWORD_API = NOT_CPL_URL + Func.USER_CHANGE_PASSWORD;
    String USER_REGISTER_API = NOT_CPL_URL + Func.USER_REGISTER;
    String USER_WX_LOGIN_API = NOT_CPL_URL + Func.USER_WX_LOGIN;
    String USER_LOGOUT_API = NOT_CPL_URL + Func.USER_LOGOUT;
    String SMS_SEND_API = NOT_CPL_URL + Func.SMS_SEND;
    String BINDING_WECHAT_API = NOT_CPL_URL + Func.BINDING_WECHAT;
    String BINDING_PHONE_API = NOT_CPL_URL + Func.BINDING_PHONE;
    String CHANGE_BIND_MOBILE_API = NOT_CPL_URL + Func.CHANGE_BIND_MOBILE;
    String UNBIND_MOBILE_API = NOT_CPL_URL + Func.UNBIND_MOBILE;
    String SET_USER_PROFILE_API = NOT_CPL_URL + Func.USER_PROFILE;

    String MSG_LIST_API = NOT_CPL_URL + Func.MSG_LIST;
    String MSG_UNREAD_API = NOT_CPL_URL + Func.MSG_UNREAD;
    String MSG_MARKED_READ_API = NOT_CPL_URL + Func.MSG_MARKED_READ;
    String MSG_SET_ALL_READ_API = NOT_CPL_URL + Func.MSG_MARKED_ALL_READ;

    String ADS_ADVERT_TAIL_API = NOT_CPL_URL + Func.ADS_ADVERT_TAIL;

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
    String NOVEL_COLUMN_DETAIL_API = NOT_CPL_URL + Func.NOVEL_COLUMN_DETAIL;

    String NOVEL_DETAILS_API = NOT_CPL_URL + Func.NOVEL_DETAILS;
    String NOVEL_CONTENT_API = NOT_CPL_URL + Func.NOVEL_CONTENT;
    String NOVEL_NOMINATE_API = NOT_CPL_URL + Func.NOVEL_NOMINATE;

    String USER_READ_TIME_API = NOT_CPL_URL + Func.USER_READ_TIME;

    String OPINION_CATEGORY_API = NOT_CPL_URL + Func.OPINION_CATEGORY;
    String OPINION_CREATE_API = NOT_CPL_URL + Func.OPINION_CREATE;


    String GET_FONT_LIST = NOT_CPL_URL + Func.GET_FONT;

    String ADVERT_AD_API = NOT_CPL_URL + Func.ADVERT_AD;
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
    String UNLOCK_CHAPTER_API = NOT_CPL_URL + Func.UNLOCK_CHAPTER;

    String READ_NOVEL_RECORD_API = NOT_CPL_URL + Func.READ_NOVEL_RECORD;

    String WELFARE_LIST_API = NOT_CPL_URL + Func.WELFARE_LIST;
    String WELFARE_COMPLETE_API = NOT_CPL_URL + Func.WELFARE_COMPLETE;
    String WELFARE_COMPLETE_ADVERT_API = NOT_CPL_URL + Func.WELFARE_COMPLETE_ADVERT;
    String WELFARE_USERLIST_API = NOT_CPL_URL + Func.WELFARE_USERLIST;
    String WELFARE_SIGNIN_API = NOT_CPL_URL + Func.WELFARE_SIGNIN_INFO;
    String WELFARE_COMPLETESINGIN_API = NOT_CPL_URL + Func.WELFARE_COMPLETESINGIN;
    String WELFARE_CHANGEADVERT_API = NOT_CPL_URL + Func.WELFARE_CHANGEADVERT;
    String WELFARE_CHANGEBONUSES_API = NOT_CPL_URL + Func.WELFARE_CHANGEBONUSES;

    /*提现*/
    String WITHDRAWAL_MENU_API = NOT_CPL_URL + Func.WITHDRAWAL_MENU;
    String WITHDRAWAL_FARE_API = NOT_CPL_URL + Func.WITHDRAWAL_FARE;
    String WITHDRAWAL_SCORE_API = NOT_CPL_URL + Func.WITHDRAWAL_SCORE;
    String WITHDRAWAL_MONEY_API = NOT_CPL_URL + Func.WITHDRAWAL_MONEY;
    String WITHDRAWAL_RECORD_API = NOT_CPL_URL + Func.WITHDRAWAL_RECORD;
    /*提现*/

    /*我的金币相关*/
    String GOLD_EARNINGS_LIST_API = NOT_CPL_URL + Func.GOLD_EARNINGS_LIST;
    /*我的推广点相关*/
    String POINT_EARNINGS_LIST_API = NOT_CPL_URL + Func.POINT_EARNINGS_LIST;
    /*我的阅读点相关*/
    String H_POINT_EARNINGS_LIST_API = NOT_CPL_URL + Func.H_POINT_EARNINGS_LIST;
    //绑银行卡
    String BANK_CREATE_API = NOT_CPL_URL + Func.BANK_CREATE;

    //分享页面信息
    String SHARE_INDEX_API = NOT_CPL_URL + Func.SHARE_INDEX;
    //邀请好友页面信息
    String WELFARE_INVITE_API = NOT_CPL_URL + Func.WELFARE_INVITE;
    //填写邀请码
    String FILLIN_INVITE_CODE_API = NOT_CPL_URL + Func.FILLIN_INVITE_CODE;
    /*已邀好友*/
    String INVITATION_INDEX_API = NOT_CPL_URL + Func.INVITATION_INDEX;

    /*免费获取会员*/
    String GET_FREE_VIP_API = NOT_CPL_URL + Func.GET_FREE_VIP;
    /*会员充值套餐*/
    String ORDER_RECHARGE_API = NOT_CPL_URL + Func.ORDER_RECHARGE;
    /*第三方支付充值套餐*/
    String ORDER_ESCROW_API = NOT_CPL_URL + Func.ORDER_ESCROW;
    /*第三方支付充值套餐明细*/
    String ORDER_ESCROW_RECORD_API = NOT_CPL_URL + Func.ORDER_ESCROW_RECORD;
    /*会员兑换裂变*/
    String ORDER_EXCHANGE_API = NOT_CPL_URL + Func.ORDER_EXCHANGE;
    /*金币会员套餐*/
    String ORDER_GOLD_EXCHANGE_API = NOT_CPL_URL + Func.ORDER_EXCHANGE_FOR_GOLD;
    /*羊毛点会员套餐*/
    String ORDER_POINT_EXCHANGE_API = NOT_CPL_URL + Func.ORDER_EXCHANGE_FOR_POINT;
    /*阅读点会员套餐*/
    String ORDER_H_POINT_EXCHANGE_API = NOT_CPL_URL + Func.ORDER_EXCHANGE_FOR_H_POINT;
    /*创建订单*/
    String ORDER_CREATE_API = NOT_CPL_URL + Func.ORDER_CREATE;
    /*CDKEY兑换*/
    String CDKEY_EXCHANGE_API = NOT_CPL_URL + Func.CDKEY_EXCHANGE;
    /*CDKEY兑换明细*/
    String CDKEY_RECORD_API = NOT_CPL_URL + Func.CDKEY_RECORD;
    /*CDKEY标记*/
    String PAY_ESCROW_CDKET = NOT_CPL_URL + Func.PAY_ESCROW_CDKET;
    /*CDKEY查询*/
    String CDKET_ORDER_QUERY = NOT_CPL_URL + Func.CDKEY_ORDER_RECORD;
    /*创建订单*/
    String ORDER_EXCHANGE_CREATE_API = NOT_CPL_URL + Func.ORDER_EXCHANGE_CREATE;
    String ORDER_ESCROW_CREATE_API = NOT_CPL_URL + Func.ORDER_ESCROW_CREATE;
    /*代付*/
    String PAY_WECHAT_API = NOT_CPL_URL + Func.PAY_WECHAT;
    String PAY_ALIPAY_API = NOT_CPL_URL + Func.PAY_ALIPAY;
    String PAY_GOLD_API = NOT_CPL_URL + Func.PAY_GOLD;
    String PAY_POINT_API = NOT_CPL_URL + Func.PAY_POINT;
    String PAY_ESCROW_WX_API_1 = NOT_CPL_URL + Func.PAY_ESCROW_WX_1;
    String PAY_ESCROW_ALI_API_1 = NOT_CPL_URL + Func.PAY_ESCROW_ALI_1;
    String PAY_ESCROW_TEST = NOT_CPL_URL + Func.PAY_ESCROW_TEST;
    /*代付订单明细*/
    String PAY_QUERY_API = NOT_CPL_URL + Func.PAY_QUERY;

    /*检测更新*/
    String VERSION_CHECK_API = NOT_CPL_URL + Func.VERSION_CHECK;
    /*APP版本详情*/
    String VERSION_DETAIL_API = NOT_CPL_URL + Func.VERSION_DETAIL;

    /*书评*/
    String APPRAISE_CREATE_API = NOT_CPL_URL + Func.APPRAISE_CREATE;
    String APPRAISE_LIST_API = NOT_CPL_URL + Func.APPRAISE_LIST;
    String APPRAISE_DETAIL_API = NOT_CPL_URL + Func.APPRAISE_DETAIL;
    String APPRAISE_REPLY_API = NOT_CPL_URL + Func.APPRAISE_REPLY;
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

    /***VIP充值***/
    int TYPE_CASH = 0;
    int TYPE_H_POINT = 1;
    int TYPE_GOLD = 1;
    int TYPE_POINT = 2;
    /***VIP充值***/

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
    String SIMULATOR = "simulator";    //模拟器标识
    String MOBILE = "mobile";           //手机号
    String CAPTCHA = "captcha";         //验证码
    String OLD_PASSWORD = "old_password";         //密码
    String PASSWORD = "password";         //密码
    String RE_PASSWORD = "re_password";    //验密码
    String DISTRIBUTION = "distribution";    //验密码
    String EVENT = "event";             //事件,event=register(登录/注册)

    String USERNAME = "username";
    String GENDER = "gender";
    String AVATAR = "avatar";
    String PERFRENCE = "like";          //用户阅读偏好

    String CODE = "code";
    String UNIONID = "unionid";
    String OPENID = "openid";

    String MOBILE_CAPTCHA = "mobile_captcha";        //验证码

    String CATEGORY = "category";
    String TYPE = "type";
    String RANK_FORM_BG = "form_boy_girl";
    String VIP_FROM_TYPE = "from_vip_meal";
    String TYPE_RANK = "type_rank";

    String N_ID = "id";
    String BOOKRACK_ID = "id";

    String NOVEL_ID = "novel_id";
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

    //书评ID---回复评论用
    String NOVEL_APPRAISE_ID = "novel_appraise_id";

    //"更多"的ID
    String COLUMN_ID = "id";

    String VOUCHER = "voucher";

}