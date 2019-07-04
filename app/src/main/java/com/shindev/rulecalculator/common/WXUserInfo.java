package com.shindev.rulecalculator.common;

import java.util.List;

/**
 * @name Login
 * @class name：com.vincent.cloud.entity
 * @class describe
 * @anthor Vincent
 * @time 2017/7/19 12:37
 * @change
 * @chang time
 * @class describe
 */

public class WXUserInfo {
    /**
     * openid : olmt4wfxS21G4VeeVX16_zUhZezY
     * nickname : 李文星
     * sex : 1
     * language : zh_CN
     * city : Shenzhen
     * province : Guangdong
     * country : CN
     * headimgurl : http://wx.qlogo.cn/mmhead/F2pduZmCoicF34vaxy7cnyLyS4T8HA80XYOM8rXDSAug/0
     * privilege : []
     * unionid : o5aWQwAa7niCIXhAIRBOwglIJ7UQ
     */

    private String openid;
    private String nickname;
    private int sex;
    private String language;
    private String city;
    private String province;
    private String country;
    private String headimgurl;
    private String unionid;
    private List<?> privilege;

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public List<?> getPrivilege() {
        return privilege;
    }

    public void setPrivilege(List<?> privilege) {
        this.privilege = privilege;
    }

    static public void setUserInfo(WXUserInfo userInfo) {
        String headUrl = userInfo.getHeadimgurl();
        SharePreferenceUtil.putString(Global.gContext, "headimgurl", headUrl);
        String openid = userInfo.getOpenid();
        SharePreferenceUtil.putString(Global.gContext, "openid", openid);
        String nickname = userInfo.getNickname();
        SharePreferenceUtil.putString(Global.gContext, "nickname", nickname);
        int sex = userInfo.getSex();
        SharePreferenceUtil.putInt(Global.gContext, "sex", sex);
        String language = userInfo.getLanguage();
        SharePreferenceUtil.putString(Global.gContext, "language", language);
        String city = userInfo.getCity();
        SharePreferenceUtil.putString(Global.gContext, "city", city);
        String province = userInfo.getProvince();
        SharePreferenceUtil.putString(Global.gContext, "province", province);
        String country = userInfo.getCountry();
        SharePreferenceUtil.putString(Global.gContext, "country", country);
        String unionid = userInfo.getUnionid();
        SharePreferenceUtil.putString(Global.gContext, "unionid", unionid);
    }

    static public WXUserInfo getUserInfo() {
        WXUserInfo userInfo = new WXUserInfo();

        userInfo.sex = SharePreferenceUtil.getInt(Global.gContext, "sex", -1);
        if (userInfo.sex == -1) {
            return null;
        }
        userInfo.headimgurl = SharePreferenceUtil.getString(Global.gContext, "headimgurl", "");
        userInfo.openid = SharePreferenceUtil.getString(Global.gContext, "openid", "");
        userInfo.nickname = SharePreferenceUtil.getString(Global.gContext, "nickname", "");
        userInfo.language = SharePreferenceUtil.getString(Global.gContext, "language", "");
        userInfo.city = SharePreferenceUtil.getString(Global.gContext, "city", "");
        userInfo.province = SharePreferenceUtil.getString(Global.gContext, "province", "");
        userInfo.country = SharePreferenceUtil.getString(Global.gContext, "country", "");
        userInfo.unionid = SharePreferenceUtil.getString(Global.gContext, "unionid", "");

        return userInfo;
    }

    static public void WxUserInitialize() {
        SharePreferenceUtil.putInt(Global.gContext, "sex", -1);
        SharePreferenceUtil.putString(Global.gContext, "headimgurl", "");
        SharePreferenceUtil.putString(Global.gContext, "openid", "");
        SharePreferenceUtil.putString(Global.gContext, "nickname", "");
        SharePreferenceUtil.putString(Global.gContext, "language", "");
        SharePreferenceUtil.putString(Global.gContext, "city", "");
        SharePreferenceUtil.putString(Global.gContext, "province", "");
        SharePreferenceUtil.putString(Global.gContext, "country", "");
        SharePreferenceUtil.putString(Global.gContext, "unionid", "");
    }
}
