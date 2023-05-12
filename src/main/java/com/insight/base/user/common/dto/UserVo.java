package com.insight.base.user.common.dto;

import com.insight.utils.pojo.user.UserBase;

import java.time.LocalDateTime;

/**
 * @author 宣炳刚
 * @date 2019/10/31
 * @remark
 */
public class UserVo extends UserBase {

    /**
     * 登录账号
     */
    private String account;

    /**
     * 用户角色, 多个角色以逗号分割
     */
    private String roleName;

    /**
     * 用户角色ID, 多个角色以逗号分割
     */
    private String roleId;

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

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
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

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
}
