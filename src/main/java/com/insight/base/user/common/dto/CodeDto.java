package com.insight.base.user.common.dto;

import com.insight.utils.pojo.base.BaseXo;

/**
 * @author 宣炳刚
 * @date 2023/3/15
 * @remark CodeDTO
 */
public class CodeDto extends BaseXo {

    /**
     * 用户登录账号
     */
    private String account;

    public CodeDto(String account) {
        this.account = account;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

}
