package com.insight.base.user.common.dto;

import com.insight.utils.pojo.base.BaseXo;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author 宣炳刚
 * @date 2018/1/4
 * @remark 用户实体类
 */
public class UserVo extends BaseXo {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户类型:0.外部用户, 1.平台用户
     */
    private Integer type;

    /**
     * 用户编码
     */
    private String code;

    /**
     * 用户姓名
     */
    @NotEmpty(message = "用户姓名/昵称不能为空")
    private String name;

    /**
     * 登录账号
     */
    @NotEmpty(message = "登录账号不能为空")
    private String account;

    /**
     * 用户手机号
     */
    private String mobile;

    /**
     * 用户E-mail
     */
    private String email;

    /**
     * 微信UnionID
     */
    private String unionId;

    /**
     * 绑定微信OpenId集合
     */
    private Map<String, String> openId;

    /**
     * 用户头像
     */
    private String headImg;

    /**
     * 备注
     */
    private String remark;

    /**
     * 用户是否内置
     */
    private Boolean isBuiltin;

    /**
     * 用户是否失效
     */
    private Boolean isInvalid;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public Map<String, String> getOpenId() {
        return openId;
    }

    public void setOpenId(Map<String, String> openId) {
        this.openId = openId;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
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

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
}
