package com.insight.base.user.common.dto;

import com.insight.util.Json;

import java.io.Serializable;

/**
 * @author 宣炳刚
 * @date 2018/4/20
 * @remark 用户功能授权DTO
 */
public class FuncPermitDto implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 导航ID
     */
    private String id;

    /**
     * 父级导航ID
     */
    private String parentId;

    /**
     * 索引,排序用
     */
    private Integer index;

    /**
     * 导航级别
     */
    private Integer type;

    /**
     * 导航名称
     */
    private String name;

    /**
     * 备注
     */
    private String remark;

    /**
     * 授权状态
     */
    private Boolean permit;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
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

    public Boolean getPermit() {
        return permit;
    }

    public void setPermit(Boolean permit) {
        this.permit = permit;
    }

    @Override
    public String toString() {
        return Json.toJson(this);
    }
}
