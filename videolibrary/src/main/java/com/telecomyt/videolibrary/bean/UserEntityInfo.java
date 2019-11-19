package com.telecomyt.videolibrary.bean;

/**
 * @author lbx
 * @date 2018/1/16.
 */

public class UserEntityInfo {

    private String nickName = "";
    private String userId = "";
    private String areaId = "";
    private String imei = "";

    public UserEntityInfo(String nickName, String userId, String areaId, String imei) {
        this.nickName = nickName;
        this.userId = userId;
        this.areaId = areaId;
        this.imei = imei;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}
