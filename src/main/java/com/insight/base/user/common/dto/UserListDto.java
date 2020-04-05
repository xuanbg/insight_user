package com.insight.base.user.common.dto;

import com.insight.utils.Json;

import java.io.Serializable;

/**
 * @author 宣炳刚
 * @date 2019/10/31
 * @remark
 */
public class UserListDto implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 用户ID
     */
    private String id;

    /**
     * 用户编码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 登录账号
     */
    private String account;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否内置
     */
    private Boolean isBuiltin;

    /**
     * 是否失效
     */
    private Boolean isInvalid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Boolean getBuiltin() {
        return isBuiltin;
    }

    public void setBuiltin(Boolean builtin) {
        isBuiltin = builtin;
    }

    public Boolean getInvalid() {
        return isInvalid;
    }

    public void setInvalid(Boolean invalid) {
        isInvalid = invalid;
    }

    @Override
    public String toString() {
        return Json.toJson(this);
    }
}
