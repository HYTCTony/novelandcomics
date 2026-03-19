//package com.nnmedia.read.utils;
//
//import android.content.Context;
//
//import com.nnmedia.novel.R;
//import com.nnmedia.read.contact.Consts;
//import com.sh.sdk.shareinstall.autologin.bean.CmccAuthThemeConfigModel;
//import com.sh.sdk.shareinstall.autologin.bean.UnicomAuthThemeConfigModel;
//
//import cn.com.chinatelecom.account.sdk.AuthViewConfig;
//import cn.com.chinatelecom.account.sdk.PrivacyAgreementConfig;
//
//
//public class AutoLoginUtils {
//
//    public static int dip2px(Context context, float dpValue) {
//        final float scale = context.getResources().getDisplayMetrics().density;
//        return (int) (dpValue * scale + 0.5f);
//    }
//
//    /**
//     * 设置移动授权页面UI
//     *
//     * @return
//     */
//    public static CmccAuthThemeConfigModel getCmccConfig() {
//        CmccAuthThemeConfigModel model = new CmccAuthThemeConfigModel();
//
//        // 导航栏
//        // 设置授权页的背景图片
//        // 参数为图片名称，如main_install，不要加上.png、.jpg等后缀，注意目录为drawable
////        model.setBgPName("main_install");
//
//        model.setAuthNavTransparent(false); // 设置授权页导航栏是否透明
//        model.setNavColor(0xffffffff); // 设置导航栏颜色
//        model.setNavText("免密登陆"); // 设置导航栏标题文字
//        model.setNavTextColor(0xffffffff); // 设置导航栏标题文字颜色
//        // 设置导航栏返回按钮图片资源，
//        // 参数为图片名称，如umcsdk_return_bg，不要加上.png、.jpg等后缀，注意目录为drawable
//        model.setNavGoBackImgPName("ic_arrow_back_24dp");
//
//        // 授权页logo
//        model.setLogoHidden(false); // 设置logo图片是否隐藏
//        model.setLogoWidth(70); // 设置logo宽度
//        model.setLogoHeight(70); // 设置logo高度
//        model.setLogoOffsetY(100); // 设置logo相对于标题栏下边缘y偏移
//        model.setLogoOffsetY_B(0); // 设置logo相对于底部y偏移
//        // 设置logo图片资源，
//        // 参数为图片名称，如main_wakeup，不要加上.png、.jpg等后缀，注意目录为drawable
//        model.setLogoImgPName("umcsdk_mobile_logo");
//
//        // 授权页号码栏
//        model.setNumberColor(0xff333333); // 设置手机号码字体颜色
//        model.setNumberSize(18); // 设置手机号码字体大小
//        model.setNumFieldOffsetY(170); // 设置手机号码相对于标题栏下边缘y偏移
//        model.setNumFieldOffsetY_B(0); // 设置手机号码相对于底部y偏移
//
//        // 授权页slogan
//        model.setSloganTextColor(0xff999999); // 设置移动slogan文字颜色
//        model.setSloganOffsetY(230); // 设置slogan相对于标题栏下边缘y偏移
//        model.setSloganOffsetY_B(0); // 设置slogan相对于底部y偏移
//
//        // 授权页登录按钮
//        model.setLogBtnText("手机号一键登录"); // 设置登录按钮文字
//        model.setLogBtnTextColor(0xffffffff); // 设置登录按钮文字颜色
//        model.setLogBtnOffsetY(270); // 设置登录按钮相对于标题栏下边缘y偏移
//        model.setLogBtnOffsetY_B(0); // 设置登录按钮相对于底部y偏移
//        // 设置登录按钮的样式，参数为对应drawable文件夹下的某个资源xml
////        model.setLogBtnPname("umcsdk_login_btn_bg");
//
//        // 切换账号
//        model.setSwitchAccTextColor(0xff999999); // 设置切换账号文字颜色
//        model.setSwitchOffsetY(330); // 设置切换账号相对于标题栏下边缘y偏移
//        model.setSwitchOffsetY_B(0); // 设置切换账号相对于底部y偏移
//
//        // 授权页隐私栏
//        model.setPrivacyState(true); // 设置隐私条款复选框是否默认选中
//        model.setPrivacyOffsetY(0); // 设置隐私条款相对于标题栏下边缘y偏移
//        model.setPrivacyOffsetY_B(30); // 设置隐私条款相对于底部y偏移
//        model.setClauseColor(0xff666666, 0xff0085d0); // 设置隐私条款名称基础文字颜色和协议文字颜色
////        model.setCbCheckPname("umcsdk_check_image"); // 设置选择框选中时的图片名字,不要加上.png、.jpg等后缀，注意目录为drawable
////        model.setCbUnCheckPname("umcsdk_uncheck_image"); // 设置选择框未选中时的图片名字,不要加上.png、.jpg等后缀，注意目录为drawable
//        model.setClauseOneParams("服务条款", Consts.USER_AGREEMENT_URL); // 设置用户自定义协议1名称和跳转url
//        model.setClauseTwoParams("隐私协议", Consts.PRIVACY_POLICY_URL); // 设置用户自定义协议2名称和跳转url
//        return model;
//    }
//
//    /**
//     * 设置联通授权页面UI
//     *
//     * @return
//     */
//    public static UnicomAuthThemeConfigModel getUnicomConfig() {
//        UnicomAuthThemeConfigModel model = new UnicomAuthThemeConfigModel();
//
//        // 以下字体大小单位为sp，控件偏移量单位为dp
//        // 导航栏
//        // 设置授权页的背景图片
//        // 参数1为图片名称，如main_wakeup，不要加上.png、.jpg等后缀
//        // 参数2为该图片资源目录，drawable或mipmap下，分别对应UnicomAuthThemeConfigModel.TYPE_DRAWABLE、UnicomAuthThemeConfigModel.TYPE_MIPMAP
////        model.setBgImg("main_wakeup",UnicomAuthThemeConfigModel.TYPE_MIPMAP);
//
//        model.setAuthNavTransparent(false); // 设置授权页导航栏是否透明
//        model.setNavColor(0xffffffff); // 设置导航栏颜色
//        model.setNavText(""); // 设置导航栏标题文字
//        model.setNavTextColor(0xffffffff); // 设置导航栏标题文字颜色
//        model.setNavTextSize(15); // 设置导航栏标题文字大小
//        // 设置导航栏返回按钮图片资源，
//        // 参数1为图片名称，如umcsdk_return_bg，不要加上.png、.jpg等后缀
//        // 参数2为该图片资源目录，drawable或mipmap下，分别对应UnicomAuthThemeConfigModel.TYPE_DRAWABLE、UnicomAuthThemeConfigModel.TYPE_MIPMAP
//        model.setNavGoBackImg("custom_return_bg", UnicomAuthThemeConfigModel.TYPE_DRAWABLE);
//
//        // 授权页logo
//        model.setLogoHidden(false); // 设置logo图片是否隐藏
//        model.setLogoWidth(70); // 设置logo宽度
//        model.setLogoHeight(70); // 设置logo高度
//        model.setLogoOffsetY(60); // 设置logo相对于标题栏下边缘y偏移
//        // 设置logo图片资源，
//        // 参数1为图片名称，如app_logo，不要加上.png、.jpg等后缀
//        // 参数2为该图片资源目录，drawable或mipmap下，分别对应UnicomAuthThemeConfigModel.TYPE_DRAWABLE、UnicomAuthThemeConfigModel.TYPE_MIPMAP
//        model.setLogoImg("unicom_logo", UnicomAuthThemeConfigModel.TYPE_DRAWABLE);
//
//        // 授权页号码栏
//        model.setNumberColor(0xff333333); // 设置手机号码字体颜色
//        model.setNumberSize(18); // 设置手机号码字体大小
//        model.setNumFieldOffsetY(160); // 设置手机号码相对于标题栏下边缘y偏移
//
//        // 授权页slogan
//        model.setSloganTextColor(0xff999999); // 设置移动slogan文字颜色
//        model.setSloganTextSize(13); // 设置移动slogan文字大小
//        model.setSloganOffsetY(200); // 设置slogan相对于标题栏下边缘y偏移
//
//        // 授权页登录按钮
//        model.setLogBtnText("手机号一键登录"); // 设置登录按钮文字
//        model.setLogBtnTextColor(0xffffffff); // 设置登录按钮文字颜色
//        model.setLogBtnTextSize(18); // 设置登录按钮文字大小
//        model.setLogBtnOffsetY(250); // 设置登录按钮相对于标题栏下边缘y偏移
//        // 设置登录按钮的样式，参数为对应drawable文件夹下的某个资源xml
//        //model.setLogBtnPname("shape_rectangle_edeef0_30");
//
//        // 其他方式登录
//        model.setOtherText("其他方式登录"); // 设置其他方式登录文字
//        model.setOtherTextColor(0xff999999); // 设置其他方式登录文字颜色
//        model.setOtherTextSize(14); // 设置其他方式登录文字大小
//        model.setOtherTextOffsetY(320); // 设置其他方式登录文字相对于标题栏下边缘y偏移
//
//        // 授权页隐私栏
//        // 参数分别对应整个隐私条款的文本、文字大小、协议的基础文字颜色、联通和自己应用协议的文字颜色
//        model.setPrivacyTextView("登录即同意《中国联通认证服务条款》和《服务条款》和《隐私协议》并授权xx应用获取本机号码", 12, 0xff666666, 0xff0085d0);
//        model.setBasePrivacy(5, 17); // 中国联通协议文字的开始和结束位置
//        model.setCustomPrivacy(18, 23, Consts.USER_AGREEMENT_URL); // 用户自己协议1文字的开始和结束位置、点击后跳转链接
//        model.setCustomPrivacyTwo(25, 30, Consts.PRIVACY_POLICY_URL); // 用户自己协议2文字的开始和结束位置、点击后跳转链接
//        model.setPrivacyState(true); // 设置隐私条款复选框是否默认选中
//        model.setPrivacyOffsetY_B(20); // 设置隐私条款相对于底部y偏移
//        // 设置选择框的样式，参数为对应drawable文件夹下的某个资源xml
////        model.setCheckBoxPname("umcsdk_checkbox_bg");
//
//        //联通协议确认弹窗配置
//        model.setDialogPrivacyTextView("登录即同意《中国联通认证服务条款》和《服务条款》和《隐私协议》", 12, 0xff666666, 0xff0085d0);
//        model.setDialogBasePrivacy(5, 17); // 中国联通协议文字的开始和结束位置
//        model.setDialogCustomPrivacy(18, 23, Consts.USER_AGREEMENT_URL); // 用户自己协议1文字的开始和结束位置、点击后跳转链接
//        model.setDialogCustomPrivacyTwo(25, 30, Consts.PRIVACY_POLICY_URL); // 用户自己协议2文字的开始和结束位置、点击后跳转链接
//
//        return model;
//    }
//
//
//    /**
//     * 设置电信授权页UI
//     * 配置说明：
//     * 1.所有配置项中，若不配置某项，则无需调用对应项的set方法，即可选择性设置某项；
//     * 2.若不配置某项参数，则传入默认值, int类型默认值为0 、String类型null或""；
//     * 3.控件的Y偏移量是相对于父控件顶部的距离，且父控件必须为RelativeLayout；
//     * <p>
//     * 以下提供动态配置的示例
//     */
//    public static AuthViewConfig getAuthViewDynamicConfig(Context context) {
//        AuthViewConfig.Builder configBuilder = new AuthViewConfig.Builder()
//                /** 免密登录界面*/
//                //设置导航栏的背景颜色, 参数说明（int viewId 控件Id(下同), int bgColor 背景色）
//                .setNavParentView(R.id.ct_account_nav_layout, 0xFFFFFFFF)
//                //设置导航栏的返回按钮，参数说明(int viewId , int iconId 图标ID)
//                .setNavGoBackView(R.id.ct_account_nav_goback, R.drawable.ct_account_auth_goback_selector)
//                //设置导航栏的标题，包括标题文本、字体大小、颜色； 参数说明(int viewId , String text 标题文本, int textColor 文本颜色,  int textSize 文本大小(单位sp,下同))
//                .setNavTitleView(R.id.ct_account_nav_title, "免密登录", 0xFF000000, 20)
//                //设置APP LOGO, 包括LOGO图标、宽高、是否隐藏、Y偏移量；参数说明(int viewId , int logoResId 图标资源id, int logoWidth 图标宽度(单位px),  int logoHeight 图标高度(单位px) , boolean logoHidden 是否隐藏 , int offsetY 距父控件顶部Y偏移值（单位px）)
//                .setLogoView(R.id.ct_account_app_logo, R.drawable.app_logo, dip2px(context, 80), dip2px(context, 80), false, dip2px(context, 105))
//                //设置脱敏号码，包括号码文本颜色、大小、Y偏移量；参数说明(int viewId , int textColor,  int textSize, int offsetY)
//                .setDesinNumberView(R.id.ct_account_desensphone, 0xFF000000, 20, dip2px(context, 200))
//                //设置品牌标识,包括Y偏移量，参数说明(int viewId , int offsetY)
//                .setBrandView(R.id.ct_account_brand_view, dip2px(context, 281))
//                //设置登录按钮，包括背景颜色、背景图片资源ID、宽、高、Y偏移量；参数说明(int viewId , int bgColor,  int bgImageResId ,int viewWidth , int viewHeight , int offsetY)
//                .setLoginParentView(R.id.ct_account_login_btn, 0, R.drawable.ct_account_auth_loginbtn_selector, 0, 0, dip2px(context, 308))
//                //设置登录按钮文本，包括文本、字体颜色、大小；参数说明(int viewId , String text,  int textColor ,int textSize)
//                .setLoginBtnView(R.id.ct_account_login_text, "本机号码一键登录", 0xFFFFFFFF, 16)
//                //设置登录按钮loading图标，包括图标资源Id  (int viewId , int iconResId)
//                .setLoginLoadingView(R.id.ct_account_login_loading, R.drawable.ct_account_login_loading_icon)
//                //设置其他登录方式，包括Y偏移量、文本、字体颜色、字体大小、是否隐藏； 参数说明(int viewId , int offsetY , String text , int textColor ,  int textSize , boolean isHidden)
//                .setOtherLoginView(R.id.ct_account_other_login_way, dip2px(context, 376), "其他登录方式", R.color.ct_account_other_text_selector, 14, false)
//                //设置底部隐私协议，包括Y偏移量（相对父控件底部的距离），参数说明 (int viewId , int offsetY)
//                .setPrivacyParentView(R.id.ct_auth_privacy_layout, dip2px(context, 20))
//                //设置底部隐私协议的复选框，包括复选框图标资源ID、默认状态（0:默认勾选，-1：默认不勾选 ）；参数说明(int viewId , int resId,  int privacyCheckBoxState)
//                .setPrivacyCheckBox(R.id.ct_auth_privacy_checkbox, R.drawable.ct_account_auth_privacy_checkbox, AuthViewConfig.STATE_DEFAULT_CHECKED)
////                //设置底部隐私协议的文本内容，包括文本、字体颜色、大小 (int viewId , String text , int textColor , int textSize)
////                .setPrivacyTextView(R.id.ct_auth_privacy_text_dynamic, "登录即同意《天翼账号服务与隐私协议》并授权[一起看书]获取本机号码" , 0xFF000000 , 12)
////                //设置底部《天翼账号服务与隐私协议》 ，包括它的开始位置、结束位置、字体颜色；参数说明(int startPos, int endPos, int textColor)
////                .setCtAccountPrivacyProtocolLink(5, 18, 0xFF0090FF)
////                //设置底部《自定义协议》 ，包括它的开始位置、结束位置、字体颜色，协议地址、协议标题；参数说明(int startPos, int endPos, int textColor , String protocolUrl , String protocolTitle)
////                .setCustomPrivacyProtocolLink(19, 26, 0xFF0090FF , "https://www.baidu.com" , "自定义协议")
//
//                /** 弹出对话框*/
//                //设置对话框，包括对话框背景颜色、按钮文字颜色、按钮文字大小；参数说明(int viewId , int bgResId , int btnTextColor , int btnTextSize)
//                .setDialogView(R.id.ct_account_dialog_layout, R.drawable.ct_account_dialog_conner_bg, 0xFF0090FF, 16)
////                //设置对话框的文本内容，包括文本内容、颜色、大小；参数说明(int viewId , String text, int textColor , int textSize)
////                .setDialogPrivacyText(R.id.ct_account_dialog_privacy_dynamic , "登录即同意《天翼账号服务与隐私协议》与《自定义协议》", 0xD9000000 , 16)
////                //设置对话框的《天翼账号服务与隐私协议》，包括它的开始位置、结束位置、字体颜色；参数说明(int startPos, int endPos, int textColor)
////                .setDialogCtAccountPrivacyProtocolLink(5, 18, 0xFF0090FF)
////                //设置对话框的《自定义协议》，它的开始位置、结束位置、字体颜色，协议地址、协议标题；参数说明(int startPos, int endPos, int textColor , String protocolUrl , String protocolTitle)
////                .setDialogCustomPrivacyProtocolLink(19, 26, 0xFF0090FF ,"https://www.baidu.com" ,"自定义协议")
//
//                // 设置底部隐私协议的文本内容
//                .setPrivacyTextViewConfig(R.id.ct_auth_privacy_text_dynamic, R.id.ct_account_dialog_privacy_dynamic, getPrivacyAgreementConfig())
//
//
//                /** 隐私协议WebViewActivity */
//                //设置隐私协议WebViewActivity的导航栏，包括导航栏ViewId、标题控件id，设置后可保持与登录界面的导航栏样式一致；参数说明(int navParentViewId, int navTitleViewId)
//                .setPrivacyWebviewActivity(R.id.ct_account_webview_nav_layout, R.id.ct_account_webview_nav_title);
//        return configBuilder.build();
//    }
//
//    /////////////////////////////////以下是设置底部及对话框隐私TextView的示例（v3.7.2新增）///////////////////////////////////////
//
//    /**
//     * 设置隐私协议文本
//     *
//     * @return
//     */
//    private static PrivacyAgreementConfig getPrivacyAgreementConfig() {
//        PrivacyAgreementConfig config = new PrivacyAgreementConfig();
//        //隐私协议文本,其中配置说明如下
//        // 1、$OAT 为运营商协议标题占位符，SDK程序默认替换为《天翼账号服务与隐私协议》，若有其它运营商协议配置需求，可添加配置；
//        // 2、$CAT 为自定义协议标题占位符，SDK程序会替换为自定义标题字段的值；
//        // 3、[应用名] ：修改为您应用的名称
//        /** 设置底部隐私TextView的属性*/
//        config.privacyText = "登录即同意$OAT与$CAT并授权[一起看书]获取本机号码"; //登录界面底部隐私协议文本，其中$OAT占位符默认替换为《天翼账号服务与隐私协议》
//        config.privacyTextColor = 0xFF000000;   //隐私协议文本的字体颜色
//        config.privacyTextSize = 12; //隐私协议文本的字体大小
//        config.operatorAgreementTitleColor = 0xFF0090FF; //运营商协议标题的字体颜色
//        config.customAgreementTitle = "《一起看书用户协议》";  //自定义协议标题
//        config.customAgreementLink = Consts.USER_AGREEMENT_URL;  //自定义协议wap页面地址
//        config.customAgreementTitleColor = 0xFF0090FF;  //自定义协议标题的字体颜色
//        /** 设置对话框隐私TextView的属性*/
//        config.dialogPrivacyText = "登录即同意$OAT与$CAT"; //对话框的隐私协议文本
//        config.dialogPrivacyTextColor = 0xFF000000; //对话框隐私协议文本的字体颜色
//        config.dialogPrivacyTextSize = 16; //对话框隐私协议文本的字体大小
//        config.dialogOperatorAgreementTitleColor = 0xFF0090FF; //对话框运营商协议标题的字体颜色
//        config.dialogCustomAgreementTitleColor = 0xFF0090FF; //对话框自定义协议标题的字体颜色
//
//
//        //若有其它运营商协议配置需求，可添加以下配置。SDK检测到异网用户会显示相应的协议标题，配置示例：
////        config.chinaMobileTitle = "《中国移动认证服务协议》";  //设置中国移动隐私协议标题
////        config.chinaMobileUrl = "https://wap.cmpassport.com/resources/html/contract.html";  //设置中国移动隐私协议wap页面地址
////        config.chinaUnicomTitle = "《中国联通服务条款及隐私协议》";  //设置中国联通隐私协议标题
////        config.chinaUnicomUrl = "https://ms.zzx9.cn/html/oauth/protocol.html"; //设置中国联通隐私协议wap页面地址
//        return config;
//    }
//}
