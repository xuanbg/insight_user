package com.insight.base.user.common.client;

import com.insight.base.user.common.config.FeignClientConfig;
import com.insight.base.user.common.dto.CodeDto;
import com.insight.base.user.common.dto.LoginDto;
import com.insight.utils.pojo.base.Reply;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
     * @param dto CodeDTO
     * @return Reply
     */
    @PostMapping("/base/auth/v1.0/codes")
    Reply generateCode(@RequestBody CodeDto dto);

    /**
     * 获取Token
     *
     * @param login 用户登录数据
     * @return Reply
     */
    @PostMapping("/base/auth/v1.0/tokens")
    Reply generateToken(@RequestBody LoginDto login);
}
