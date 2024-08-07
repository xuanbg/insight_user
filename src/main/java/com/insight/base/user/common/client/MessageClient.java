package com.insight.base.user.common.client;

import com.insight.base.user.common.config.FeignClientConfig;
import com.insight.utils.pojo.base.Reply;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author 宣炳刚
 * @date 2019-08-31
 * @remark 消息中心Feign客户端
 */
@FeignClient(name = "common-message", configuration = FeignClientConfig.class)
public interface MessageClient {

    /**
     * 验证短信验证码
     *
     * @param key 验证参数,MD5(type + mobile + code)
     * @return Reply
     */
    @GetMapping("/common/message/v1.0/codes/{key}/status?isCheck=false")
    Reply verifySmsCode(@PathVariable String key);
}
