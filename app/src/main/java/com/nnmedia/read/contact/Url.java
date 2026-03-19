package com.nnmedia.read.contact;

import rxhttp.wrapper.annotation.DefaultDomain;
import rxhttp.wrapper.annotation.Domain;

public class Url {
    @DefaultDomain //设置为默认域名-
//    public static String baseurl = "https://klj.htxs.fun";
    //    @DefaultDomain //设置为测试域名
    public static String baseurl = "";
    @Domain(name = "BaseUrlGX")
    public static String upload = "https://gx.htxs.app";
    @Domain(name = "BaseUrlYM")
    public static String dynamic = "https://ym.htxs.app";
    @Domain(name = "BaseUrlYM2")
    public static String dynamic2 = "https://ym.htxs.life";
    @Domain(name = "BaseUrlYM3")
    public static String dynamic3 = "https://ym.htxs.website";
    @Domain(name = "BaseUrlYM4")
    public static String dynamic4 = "https://ym.htxs.lol";
    @Domain(name = "BaseUrlYM5")
    public static String dynamic5 = "https://ym.htxs.one";
//    @Domain(name = "BaseUrlYM")
//    public static String dynamic = "https://dom.htxs.online";
//    @Domain(name = "BaseUrlYM2")
//    public static String dynamic2 = "https://dom.htxs.online";
}