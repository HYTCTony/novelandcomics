package com.huli.foxread.entity;

import java.io.Serializable;

public class WUser implements Serializable {

    private String id;                  //用户id
    private String nickname;            //用户昵称
    private String image;               //用户图片
    private String city;                //用户城市
    private String birthday;            //用户生日，需app自行转换成年龄（格式：2019-10-10）
    private String signature;           //用户签名名片
    private int gender;                 //性别:1:男,2:女
    private int is_vip;                 //是否是VIP，0，不是，1是
    private String vip_expiration;      //vip过期时间,不是VIP或已过期时不显示（格式：2019-12-11 10:28:09）
    private int play_count;             //当日已播放次数（增加播放次数时请求相关的接口）
    private int playable_number;        //可播放次数
    private String tel;                 //手机号
    private String safety_code;         //安全码/锁屏码
    private String invite_code;         //邀请码
    private String invite_url;          //邀请链接
    private String exp;                 //经验
    private String type_name;           //用户等级名称
    private int grade;                  //当前用户等级
    private String follow;              //关注
    private String fans;                //粉丝
    private String likes;               //点赞
    private String my_inviter;          //我的邀请人代码
    private int album_number;           //相册数量
    private int video_number;           //上传的短视频数量
    private int likes_number;           //喜欢的短视频数量

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getIs_vip() {
        return is_vip;
    }

    public void setIs_vip(int is_vip) {
        this.is_vip = is_vip;
    }

    public String getVip_expiration() {
        return vip_expiration;
    }

    public void setVip_expiration(String vip_expiration) {
        this.vip_expiration = vip_expiration;
    }

    public int getPlay_count() {
        return play_count;
    }

    public void setPlay_count(int play_count) {
        this.play_count = play_count;
    }

    public int getPlayable_number() {
        return playable_number;
    }

    public void setPlayable_number(int playable_number) {
        this.playable_number = playable_number;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getSafety_code() {
        return safety_code;
    }

    public void setSafety_code(String safety_code) {
        this.safety_code = safety_code;
    }

    public String getInvite_code() {
        return invite_code;
    }

    public void setInvite_code(String invite_code) {
        this.invite_code = invite_code;
    }

    public String getInvite_url() {
        return invite_url;
    }

    public void setInvite_url(String invite_url) {
        this.invite_url = invite_url;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getFollow() {
        return follow;
    }

    public void setFollow(String follow) {
        this.follow = follow;
    }

    public String getFans() {
        return fans;
    }

    public void setFans(String fans) {
        this.fans = fans;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getMy_inviter() {
        return my_inviter;
    }

    public void setMy_inviter(String my_inviter) {
        this.my_inviter = my_inviter;
    }

    public int getAlbum_number() {
        return album_number;
    }

    public void setAlbum_number(int album_number) {
        this.album_number = album_number;
    }

    public int getVideo_number() {
        return video_number;
    }

    public void setVideo_number(int video_number) {
        this.video_number = video_number;
    }

    public int getLikes_number() {
        return likes_number;
    }

    public void setLikes_number(int likes_number) {
        this.likes_number = likes_number;
    }
}
