package com.insight.base.user.common.dto;

import com.insight.util.Json;

import java.io.Serializable;

/**
 * @author 宣炳刚
 * @date 2017/9/7
 * @remark 登录数据DTO
 */
public class LoginDto implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 登录账号
     */
    private String account;

    /**
     * 签名
     */
    private String signature;

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

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return Json.toJson(this);
    }
}
