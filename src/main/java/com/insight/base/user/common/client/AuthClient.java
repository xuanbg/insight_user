package com.insight.base.user.common.client;

import com.insight.base.user.common.config.FeignClientConfig;
import com.insight.base.user.common.dto.LoginDto;
import com.insight.util.pojo.Reply;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 宣炳刚
 * @date 2019-08-31
 * @remark 消息中心Feign客户端
 */
@FeignClient(name = "base-auth", configuration = FeignClientConfig.class)
public interface AuthClient {

    /**
     * 获取Code
     *
     * @param account 用户登录账号
     * @return Reply
     */
    @GetMapping("/base/auth/v1.0/tokens/codes")
    Reply getCode(@RequestParam String account);

    /**
     * 获取Token
     *
     * @param login 用户登录数据
     * @return Reply
     */
    @PostMapping("/base/auth/v1.0/tokens")
    Reply getToken(@RequestBody LoginDto login);
}
