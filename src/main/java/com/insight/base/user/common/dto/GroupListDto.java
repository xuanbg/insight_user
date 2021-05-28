package com.insight.base.user.common.dto;

import com.insight.utils.pojo.BaseXo;

/**
 * @author 宣炳刚
 * @date 2019/12/4
 * @remark 用户组列表DTO
 */
public class GroupListDto extends BaseXo {

    /**
     * 用户组ID
     */
    private Long id;

    /**
     * 用户组编码
     */
    private String code;

    /**
     * 用户组名称
     */
    private String name;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否内置
     */
    private Boolean isBuiltin;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
}
