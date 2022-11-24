package com.insight.base.user.common.dto;

import com.insight.utils.pojo.user.User;

/**
 * @author 宣炳刚
 * @date 2021/12/8
 * @remark
 */
public class UserDto extends User {

    /**
     * 租户ID
     */
    private Long tenantId;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}
