package com.huli.foxread.contact;

public interface Func {

    /**
     * 用途：使用手机唯一标识符注册/登录（POST）
     * 参数：
     * unique_id---手机唯一标识符
     * return 用户信息
     */
    String USER_VISITOR_LOGIN = "/login/visitor";

    /**
     * 用途：设置性别---来书籍推荐
     * 头：token---token
     * 参数：
     * gender---性别:0=男,1=女
     */
    String USER_SET_GENDER = "/user/setGender";

    /**
     * 用途：用户信息
     * 头：token---token(Y)
     * 参数：
     * return 用户信息
     */
    String USER_INFO = "/user/index";

    /**
     * 用途：用户资金详情
     * 头：token---token(Y)
     * 参数：
     * return 用户信息
     */
    String USER_CAPITAL = "/user/capital";

    /**
     * 用途：手机号登录（POST）
     * 参数：
     * mobile (Y）--- 手机号
     * captcha (Y）--- 验证码
     * identifier( Y）--- 手机唯一标志符
     */
    String USER_MOBILE_LOGIN = "/login/mobile";

    /**
     * 用途：正式用户登出（POST）
     * 头：token---token(Y)
     * 参数：
     */
    String USER_LOGOUT = "/login/logout";

    /**
     * 用途：登录验证码
     * 参数：
     * mobile (Y）--- 手机号
     * event (Y）--- 事件,更换绑定手机：untying，绑定手机：bind，注册：register，登陆：login
     */
    String SMS_SEND = "/sms/send";

    /**
     * 用途：绑定手机
     * 头：token---token(Y)
     * 参数：
     * mobile (Y）--- 手机号
     * captcha (Y）--- 验证码
     */
    String BIND_MOBILE = "/user/changemobile";

    /**
     * 用途：解绑手机（配合 BIND_MOBILE 才能完成手机号更换操作）
     * 头：token---token(Y)
     * 参数：
     * mobile (Y）--- 手机号
     * captcha (Y）--- 验证码
     */
    String UNBIND_MOBILE = "/user/untyingmobile";


    /**
     * 用途：填写邀请码(POST)
     * 头：token---token(Y)
     * 参数：
     * code (Y）--- 邀请码
     */
    String FILLIN_INVITE_CODE = "/user/bindingInvitationCode";

    /**
     * 用途：修改会员个人信息(POST)
     * 头：token---token(Y)
     * 参数：（三个参数至少填一个）
     * username---用户名
     * avatar---头像
     * gender---性别(0=男,1=女)
     */
    String USER_PROFILE = "/user/profile";


    /**
     * 用途：消息列表(GET)
     * 头：token---token(Y)
     * 参数：
     * type (Y）--- 1=全部,2=未读,默认1
     * page (Y）--- 页码,默认1
     * page_size (Y）--- 页数据量,默认15
     */
    String MSG_LIST = "/message/read";


    /**
     * 开屏广告(GET)
     */
    String ADS_TAIL = "/advertisement/tail";
    /**
     * 横幅广告(GET)
     */
    String ADS_BANNER = "/advertisement/banner";
    /**
     * 插屏广告(GET)
     */
    String ADS_PLAQUE = "/advertisement/plaque";
    /**
     * 信息流广告(GET)
     */
    String ADS_INFO = "/advertisement/info";


    /**
     * 用途：系统头像列表
     * 头：token(Y)
     */
    String AVATAR_LIST = "/Avatar/avatarList";

    /**
     * 用途：首页(GET)
     * 头：token(Y)
     * 参数：
     * type---类型:1=男生,2=女生,3=图书,4=精选(Y)
     * page---精选推荐列表页码(Y/N)
     * page_size---精选推荐列表,一页数据量(Y/N)
     */
    String INDEX_PAGE = "/index/index";

    /**
     * 用途：轮播图
     * 头：token(Y)
     * 参数：
     * position---广告位置:1=男生,2=女生,3=图书,4=精选(Y)
     */
    String BANNER_READ = "/banner/read";

    /**
     * 用途：高分精选
     * 头：token(Y)
     * 参数：
     * page --- 页码,默认1
     * list_rows --- 每页数据量，默认15
     * type --- 1=男生,2=女生,3=图书，精选的时候 男的就传男的女的就传女的
     */
    String NOVEL_POPULAR = "/novel/popular";

    /**
     * 用途：排行榜(GET)
     * 头：token(Y)
     * 参数：
     * type --- 1=热门排行榜,2=完结热门排行榜,3=新书热门排行榜4=热搜排行榜
     * form --- 1=男生 , 2=女生
     */
    String INDEX_RANKING = "/index/rankingList";

    /**
     * 用途：分类(GET)
     * 头：token(Y)
     * 参数：
     */
    String NOVEL_CATEGORY = "/novel/category";

    /**
     * 用途：子分类(POST)
     * 头：token(Y)
     * 参数：
     * id --- 大分类ID
     */
    String NOVEL_CATEGORY_SUB = "/novel/sub";

    /**
     * 用途：小说按条件刷选(POST)
     * 头：token(Y)
     * 参数：
     * is_parent --- 1为父分类(查看全部的意思)，0不是父分类
     * classify_id --- 分类id
     * word_calssify --- 字数分类:1=100万字以下,2=100-200万字,3=200-300万字,4=300万字以上
     * is_end --- 是否完结:1=已完结,0=未完结
     * status --- 状态:1=按热度,2=按评分,3=新上架
     * page --- 页码,默认1
     * page_size --- 每页数据量，默认15
     */
    String NOVEL_CHOICE = "/novel/Choice";

    /**
     * 用途：小说按条件刷选(POST)
     * 头：token(Y)
     * 参数：
     * keyword --- 关键字
     * page --- 页码,默认1
     * page_size --- 每页数据量，默认15
     */
    String NOVEL_KEYWORD = "/novel/keyword";

    /**
     * 用途：热门(POST)
     * 头：token（Y）
     * 参数：
     * page --- 页码,默认1
     * list_rows --- 每页数据量，默认15
     * type --- 1=男生,2=女生,3=图书，精选的时候 男的就传男的女的就传女的
     */
    String NOVEL_HOT = "/novel/hot";

    /**
     * 用途：获取小说热搜关键词(GET)
     * 头：token（Y）
     * 参数：
     */
    String KEYWORD_INDEX = "/keyword/index";

    /**
     * 用途：男生新品|女生新品|图书新品|精选新品(GET)
     * 头：token(Y)
     * 参数：
     */
    String NOVEL_COLUMN_BOYNEW = "/novel_column/boyNew";
    String NOVEL_COLUMN_GIRLNEW = "/novel_column/girlNew";
    String NOVEL_COLUMN_LIBNEW = "/novel_column/boyNew";
    String NOVEL_COLUMN_SELECTIONNEW = "/novel_column/boyNew";

    /**
     * 用途：男生完结|女生完结(GET)
     * 头：token(Y)
     * 参数：
     */
    String NOVEL_COLUMN_BOYEND = "/novel_column/boyEnd";
    String NOVEL_COLUMN_GIRLEND = "/novel_column/girlEnd";

    /**
     * 用途：小说详情(GET)
     * 头：token---token(Y)
     * 参数：
     * id---小说id
     */
    String NOVEL_DETAILS = "/novel/novelDetails";
    /**
     * 用途：小说内容(POST)
     * 头：token---token(Y)
     * 参数：
     * novel_id---小说id
     * chapter_id---章节id
     * chapter---小说章节
     */
    String NOVEL_CONTENT = "/novel/novelContent";

    /**
     * 用途：小说详情----相关推荐(GET)
     * 头：token---token(Y)
     * 参数：
     * id---小说id
     */
    String NOVEL_NOMINATE = "/shove/nominate";

    /**
     * 用途：获取反馈分类
     * 头：token---token(Y)
     * 参数：
     * id---小说id
     */
    String FEEDBACK_CATEGORY = "/opinion/category";
    /**
     * 用途：提交反馈
     * 头：token---token(Y)
     * 参数：
     * id---小说id
     */
    String FEEDBACK_CREAT = "/opinion/create";

    /**
     * 用途：（单个或批量）加入书架(GET)
     * 头：token---token(Y)
     * 参数：
     * id---小说id
     */
    String BOOKRACK_ADD = "/bookshelf/create";
    /**
     * -----------------------------废弃
     * 用途：批量加入书架(GET)
     * 头：token---token(Y)
     * 参数：
     * id---小说id
     */
    String BOOKRACK_ADD_BATCH = "/bookshelf/addBatch";

    /**
     * 用途：删除书架书籍(POST)
     * 头：token---token(Y)
     * 参数：
     * ids---小说id数组
     */
    String BOOKRACK_DEL = "/bookshelf/delete";

    /**
     * 用途：获取书架书籍(GET)
     * 头：token---token(Y)
     * 参数：
     */
    String BOOKRACK_GETLIST = "/bookshelf/read";

    /**
     * 书架，推荐一本书
     * 头：token---token(Y)
     * 参数：
     */
    String SPECIAL_BOOK = "/shove/one";


    /**
     * 用途：小说阅读记录(GET)
     * 头：token---token(Y)
     * 参数：
     * page --- 页码,默认1
     * page_size --- 页数据量，默认15
     */
    String READ_NOVEL_RECORD = "/novel/novelRecord";


    /**
     * 用途：福利任务列表(GET)
     * 头：token---token(Y)
     * 参数：
     */
    String WELFARE_LIST = "/welfare/list";

    /**
     * 用途：大转盘抽奖(GET)
     * 头：token---token(Y)
     * 参数：
     */
    String WELFARE_LUCKDRAW = "/welfare/luckDraw";


    /**
     * 用途：大转盘列表(GET)
     * 头：token---token(Y)
     * 参数：
     */
    String WELFARE_LUCKDRAWLIST = "/welfare/luckDrawList";


    /**
     * 用途：看小视频得金币(GET)
     * 头：token---token(Y)
     * 参数：
     */
    String WELFARE_VIDEO = "/welfare/welfareVideo";


    /**
     * 用途：阅读30秒倒计时奖励(GET)
     * 头：token---token(Y)
     * 参数：
     */
    String WELFARE_READING = "/welfare/welfareReading";


    /**
     * 用途：获取用户阅读今日阅读时长
     * 头：token---token(Y)
     * 参数：
     */
    String USER_READ_TIME = "/user/time";
    /**
     * 用途：新用户签到七天福利(GET)
     * 头：token---token(Y)
     * 参数：
     */
    String WELFARE_NEWUSERSIGN = "/welfare/welfareNewUserSign";

    /**
     * 用途：完成福利任务(GET)
     * 头：token---token(Y)
     * 参数：
     * id---任务id
     * lower_id --- 子任务id，没有则为空
     */
    String WELFARE_COMPLETE = "/welfare/complete";

    /**
     * 用途：个人中心福利模块(GET)
     * 头：token---token(Y)
     * 参数：
     */
    String WELFARE_USERLIST = "/welfare/userlist";

    /**
     * 用途：普通   签到详情(GET)
     * 头：token---token(Y)
     * 参数：
     */
    String WELFARE_SIGNIN_INFO = "/welfare/signIn";

    /**
     * 用途：普通签到提交(GET)
     * 头：token---token(Y)
     * 参数：
     */
    String WELFARE_COMPLETESINGIN = "/welfare/completeSingIn";

    /**
     * 用途：邀请详情(GET)
     * 头：token---token(Y)
     * 参数：
     */
    String WELFARE_INVITE = "/welfare/invite";


    /**
     * 用途：小说章节列表(GET)
     * 头：token---token(Y)
     */
    String NOVEL_NOVELCHAPTERLIST = "/novel/novelChapterList";

    /**
     * 记录阅读时间
     * 头：token---token(Y)
     */
    String RECORD_DURATION= "/duration/create";
    /**
     * 提交阅读记录
     * 头：token---token(Y)
     */
    String RECORD_CREATE= "/record/create";
    /**
     * 获取阅读记录
     * 头：token---token(Y)
     */
    String RECORD_READ= "/record/read";
    /**
     * 删除阅读记录
     * 头：token---token(Y)
     */
    String RECORD_DELETE= "/record/delete";


    /**
     * 用途：金币提现，提现菜单(GET)
     * 头：token---token(Y)
     */
    String WITHDRAWAL_MENU = "/withdrawal/menu";
    /**
     * 用途：现金提现，提现菜单（套餐）(GET)
     * 头：token---token(Y)
     */
    String WITHDRAWAL_FARE = "/withdrawal/fare";

    /**
     * 用途：余额提现(POST)
     * 头：token---token(Y)
     * 参数：
     * want_money --- 提现金额
     */
    String WITHDRAWAL_MONEY = "/withdrawal/money";

    /**
     * 用途：金币提现(POST)
     * 头：token---token(Y)
     * 参数：
     * id --- 金币提现套餐ID
     */
    String WITHDRAWAL_SCORE = "/withdrawal/score";

    /**
     * 用途：提现记录(现金和金币)(POST)
     * 头：token---token(Y)
     * 参数：
     */
    String WITHDRAWAL_RECORD = "/withdrawal/read";

    /**
     * 用途：我的金币相关信息（GET）
     * 头：token---token(Y)
     * 参数：
     */
    String GOLD_COIN_INFO = "/score/read";

    /**
     * 用途：金币收益列表（GET）
     * 头：token---token(Y)
     * 参数：
     */
    String GOLD_EARNINGS_LIST = "/score/index";

    /**
     * 用途：绑定银行卡（POST）
     * 头：token---token(Y)
     * 参数：
     * id_card --- 持卡人身份证号码
     * bank_name --- 开户银行名称
     * address --- 开户银行地址
     * account --- 银行卡账户
     * name --- 持卡人姓名
     */
    String BANK_CREATE = "/bank/create";


    /**
     * 用途：已邀好友
     * 头：token---token(Y)
     * 参数：
     */
    String INVITATION_INDEX = "/invitation/index";


    /**
     * 用途：充值列表（GET）
     * 头：token---token(Y)
     * 参数：
     */
    String ORDER_RECHARGE = "/order/recharge";

    /**
     * 用途：创建充值订单（POST）
     * 头：token---token(Y)
     * 参数：
     * id --- 套餐ID
     */
    String ORDER_CREATE = "/order/create";

    /**
     * 用途：微信支付（POST）
     * 头：token---token(Y)
     * 参数：
     * order_id --- 套餐ID
     */
    String PAY_WECHAT = "/pay/wx";

    /**
     * 用途：支付宝支付（POST）
     * 头：token---token(Y)
     * 参数：
     * order_id --- 套餐ID
     */
    String PAY_ALIPAY = "/pay/ali";


}
