package com.insight.base.user.common.dto;

import com.insight.utils.pojo.user.User;

import java.util.List;
import java.util.Objects;

/**
 * @author 宣炳刚
 * @date 2021/12/8
 * @remark
 */
public class UserDto extends User {

    /**
     * 用户所属组织机构ID
     */
    private Long orgId;

    /**
     * 用户授权角色ID集合
     */
    private List<Long> roleIds;

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }

    public Boolean mobileEquals(String mobile){
        return Objects.equals(getMobile(), mobile);
    }

    public Boolean equals(UserVo dto) {
        return Objects.equals(getName(), dto.getName()) && mobileEquals(dto.getMobile());
    }
}
