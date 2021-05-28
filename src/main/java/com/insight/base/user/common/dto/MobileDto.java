package com.insight.base.user.common.dto;

import com.insight.utils.pojo.BaseXo;

/**
 * @author 宣炳刚
 * @date 2019-09-06
 * @remark 手机验证码DTO
 */
public class MobileDto extends BaseXo {

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 验证参数,MD5(type + mobile + code)
     */
    private String key;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
