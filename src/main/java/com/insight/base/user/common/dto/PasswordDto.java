package com.insight.base.user.common.dto;

import com.insight.utils.Json;

import java.io.Serializable;

/**
 * @author 宣炳刚
 * @date 2019-09-06
 * @remark 密码DTO
 */
public class PasswordDto implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 用户ID
     */
    private String id;

    /**
     * 验证参数,MD5(type + mobile + code)
     */
    private String key;

    /**
     * 新密码(MD5值)
     */
    private String password;

    /**
     * 旧密码(MD5值)
     */
    private String old;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 租户ID
     */
    private String tenantId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOld() {
        return old;
    }

    public void setOld(String old) {
        this.old = old;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public String toString(){
        return Json.toJson(this);
    }
}
