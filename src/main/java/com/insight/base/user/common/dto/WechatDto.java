package com.insight.base.user.common.dto;

import com.insight.utils.pojo.base.BaseXo;

/**
 * @author 宣炳刚
 * @date 2023/5/4
 * @remark 微信DTO
 */
public class WechatDto extends BaseXo {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 微信APPID
     */
    private String weChatAppId;

    /**
     * 微信授权码
     */
    private String code;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWeChatAppId() {
        return weChatAppId;
    }

    public void setWeChatAppId(String weChatAppId) {
        this.weChatAppId = weChatAppId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
